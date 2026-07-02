package org.sharesanitizer.app.sanitizer

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.UUID

object ImageSanitizer {

    suspend fun sanitizeImage(
        context: Context,
        uri: Uri,
        exportFormat: ExportFormat,
        quality: Int,
        filenameSuffix: String
    ): ImageSanitizeResult = withContext(Dispatchers.IO) {
        try {
            val originalFilename = getFileName(context, uri) ?: "image_${UUID.randomUUID().toString().take(8)}"
            val safeBaseName = originalFilename
                .substringBeforeLast(".", originalFilename)
                .sanitizeFileComponent()
                .ifBlank { "image_${UUID.randomUUID().toString().take(8)}" }

            // Determine format
            val mimeType = context.contentResolver.getType(uri)?.lowercase() ?: "image/jpeg"
            if (mimeType.contains("gif")) {
                return@withContext ImageSanitizeResult.Error(
                    uri,
                    "Animated GIF sanitization is not supported yet. Exporting it would flatten animation to a single frame."
                )
            }

            val actualExportFormat = if (exportFormat == ExportFormat.ORIGINAL) {
                when {
                    mimeType.contains("png") -> ExportFormat.PNG
                    mimeType.contains("webp") -> ExportFormat.WEBP
                    else -> ExportFormat.JPEG
                }
            } else {
                exportFormat
            }
            
            val ext = when (actualExportFormat) {
                ExportFormat.PNG -> "png"
                ExportFormat.WEBP -> "webp"
                else -> "jpg"
            }
            
            val safeSuffix = filenameSuffix.sanitizeFileComponent().ifBlank { "_clean" }
            val outFilename = "$safeBaseName$safeSuffix.$ext"
            
            // Create cache file
            val cacheDir = File(context.cacheDir, "shared_images").apply { mkdirs() }
            val outFile = uniqueFile(cacheDir, outFilename)
            
            // Decode without metadata (just raw pixels)
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            context.contentResolver.openInputStream(uri)?.use { 
                BitmapFactory.decodeStream(it, null, options)
            }
            if (options.outWidth <= 0 || options.outHeight <= 0) {
                throw IOException("Unsupported or invalid image file")
            }
            
            val originalSize = getFileSize(context, uri)
            val orientation = readExifOrientation(context, uri)
            
            // Prevent OOM for huge images (e.g. > 4000px)
            var scale = 1
            while (options.outWidth / scale > 4000 || options.outHeight / scale > 4000) {
                scale *= 2
            }
            
            val decodeOptions = BitmapFactory.Options().apply {
                inSampleSize = scale
                // Ensure no color space weirdness or exif is kept
            }
            
            val decodedBitmap = context.contentResolver.openInputStream(uri)?.use {
                BitmapFactory.decodeStream(it, null, decodeOptions)
            } ?: throw Exception("Failed to decode image")
            val bitmap = applyExifOrientation(decodedBitmap, orientation)
            
            val compressFormat = when (actualExportFormat) {
                ExportFormat.PNG -> Bitmap.CompressFormat.PNG
                ExportFormat.WEBP -> {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                        if (quality == 100) Bitmap.CompressFormat.WEBP_LOSSLESS else Bitmap.CompressFormat.WEBP_LOSSY
                    } else {
                        @Suppress("DEPRECATION")
                        Bitmap.CompressFormat.WEBP
                    }
                }
                else -> Bitmap.CompressFormat.JPEG
            }
            
            val safeQuality = quality.coerceIn(1, 100)
            FileOutputStream(outFile).use { out ->
                if (!bitmap.compress(compressFormat, safeQuality, out)) {
                    throw IOException("Failed to write sanitized image")
                }
            }
            bitmap.recycle()
            
            ImageSanitizeResult.Success(
                originalUri = uri,
                originalName = originalFilename,
                originalSize = originalSize,
                outputFile = outFile,
                outputSize = outFile.length(),
                format = actualExportFormat
            )
        } catch (e: Exception) {
            ImageSanitizeResult.Error(uri, e.message ?: "Unknown error")
        }
    }
    
    private fun getFileName(context: Context, uri: Uri): String? {
        var name: String? = null
        if (uri.scheme == "content") {
            try {
                context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        val index = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                        if (index != -1) {
                            name = cursor.getString(index)
                        }
                    }
                }
            } catch (e: Exception) {
                // ignore
            }
        }
        if (name == null) {
            name = uri.path?.substringAfterLast('/')
        }
        return name
    }
    
    private fun getFileSize(context: Context, uri: Uri): Long {
        if (uri.scheme == "content") {
            try {
                context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        val index = cursor.getColumnIndex(android.provider.OpenableColumns.SIZE)
                        if (index != -1) {
                            return cursor.getLong(index)
                        }
                    }
                }
            } catch (e: Exception) {
                // ignore
            }
        }
        if (uri.scheme == "file") {
            return uri.path?.let { File(it).length() } ?: 0L
        }
        return 0L
    }

    private fun readExifOrientation(context: Context, uri: Uri): Int {
        return try {
            context.contentResolver.openInputStream(uri)?.use { input ->
                ExifInterface(input).getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL
                )
            } ?: ExifInterface.ORIENTATION_NORMAL
        } catch (e: Exception) {
            ExifInterface.ORIENTATION_NORMAL
        }
    }

    private fun applyExifOrientation(bitmap: Bitmap, orientation: Int): Bitmap {
        val matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.preScale(-1f, 1f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
            ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.preScale(1f, -1f)
            ExifInterface.ORIENTATION_TRANSPOSE -> {
                matrix.postRotate(90f)
                matrix.preScale(-1f, 1f)
            }
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
            ExifInterface.ORIENTATION_TRANSVERSE -> {
                matrix.postRotate(-90f)
                matrix.preScale(-1f, 1f)
            }
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
            else -> return bitmap
        }

        return try {
            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
                .also { transformed ->
                    if (transformed != bitmap) bitmap.recycle()
                }
        } catch (e: Exception) {
            bitmap
        }
    }

    private fun uniqueFile(directory: File, preferredName: String): File {
        val safeName = preferredName.sanitizeFileComponent().ifBlank {
            "image_${UUID.randomUUID().toString().take(8)}.jpg"
        }
        val baseName = safeName.substringBeforeLast(".", safeName)
        val extension = safeName.substringAfterLast(".", "")
        var candidate = File(directory, safeName)
        var index = 1

        while (candidate.exists()) {
            val indexedName = if (extension.isBlank()) {
                "${baseName}_$index"
            } else {
                "${baseName}_$index.$extension"
            }
            candidate = File(directory, indexedName)
            index++
        }

        return candidate
    }

    private fun String.sanitizeFileComponent(): String {
        return replace(Regex("[\\\\/:*?\"<>|\\p{Cntrl}]"), "_")
            .replace(Regex("\\s+"), " ")
            .trim()
            .take(120)
    }
}

enum class ExportFormat {
    ORIGINAL, JPEG, PNG, WEBP
}

sealed class ImageSanitizeResult {
    data class Success(
        val originalUri: Uri,
        val originalName: String,
        val originalSize: Long,
        val outputFile: File,
        val outputSize: Long,
        val format: ExportFormat
    ) : ImageSanitizeResult()
    
    data class Error(val uri: Uri, val error: String) : ImageSanitizeResult()
}

