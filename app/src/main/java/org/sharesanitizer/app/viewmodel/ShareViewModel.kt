package org.sharesanitizer.app.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import org.sharesanitizer.app.sanitizer.CommunityUrlRules
import org.sharesanitizer.app.sanitizer.ExportFormat
import org.sharesanitizer.app.sanitizer.ImageSanitizeResult
import org.sharesanitizer.app.sanitizer.ImageSanitizer
import org.sharesanitizer.app.sanitizer.SanitizeResult
import org.sharesanitizer.app.sanitizer.TextCleaner
import org.sharesanitizer.app.sanitizer.UrlSanitizer
import org.sharesanitizer.app.settings.SettingsRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ShareViewModel(application: Application) : AndroidViewModel(application) {
    private val settingsRepository = SettingsRepository(application)

    private val _textResult = MutableStateFlow<SanitizeResult?>(null)
    val textResult: StateFlow<SanitizeResult?> = _textResult.asStateFlow()

    private val _imageResults = MutableStateFlow<List<ImageSanitizeResult>>(emptyList())
    val imageResults: StateFlow<List<ImageSanitizeResult>> = _imageResults.asStateFlow()

    private val _isProcessing = MutableStateFlow(false)
    val isProcessing: StateFlow<Boolean> = _isProcessing.asStateFlow()

    private val _history = MutableStateFlow<List<String>>(emptyList())
    val history: StateFlow<List<String>> = _history.asStateFlow()

    private val _lastClearedImages = MutableStateFlow<List<ImageSanitizeResult>>(emptyList())
    val canUndoImages: StateFlow<Boolean> = _lastClearedImages
        .map { it.isNotEmpty() }
        .stateIn(viewModelScope, SharingStarted.Lazily, false)

    private val _notifications = MutableSharedFlow<String>()
    val notifications = _notifications.asSharedFlow()

    fun sanitizeText(text: String) {
        viewModelScope.launch {
            val trim = settingsRepository.trimWhitespace.first()
            val collapse = settingsRepository.collapseLines.first()
            val stripInvisible = settingsRepository.stripInvisibleChars.first()
            val unwrapRedirects = settingsRepository.unwrapRedirects.first()
            val customParams = settingsRepository.customTrackingParams.first()
            val communityRules = CommunityUrlRules.load(getApplication())

            var processedText = text
            var invisibleCharsRemoved = 0

            if (stripInvisible) {
                val cleanResult = TextCleaner.clean(processedText)
                processedText = cleanResult.cleaned
                invisibleCharsRemoved = cleanResult.removedCount
            }

            val result = UrlSanitizer.sanitizeText(
                processedText,
                additionalParams = customParams,
                trimWhitespace = trim,
                collapseLines = collapse,
                communityRules = communityRules,
                unwrapRedirects = unwrapRedirects
            )

            val finalResult = result.copy(
                original = text,
                invisibleCharsRemoved = invisibleCharsRemoved
            )

            _textResult.value = finalResult
            if (finalResult.cleaned != text || invisibleCharsRemoved > 0) {
                addHistory("Sanitized text: removed tracking data")
                _notifications.emit("Text sanitized successfully")
            } else {
                addHistory("Text was already clean")
                _notifications.emit("Text was already clean")
            }
        }
    }

    fun clearText() {
        _textResult.value = null
    }

    fun sanitizeImages(uris: List<Uri>) {
        if (uris.isEmpty()) return
        viewModelScope.launch {
            _isProcessing.value = true
            _imageResults.value = emptyList()

            val formatStr = settingsRepository.exportFormat.first()
            val format = try { ExportFormat.valueOf(formatStr) } catch (_: Exception) { ExportFormat.ORIGINAL }
            val quality = settingsRepository.imageQuality.first()
            val suffix = settingsRepository.filenameSuffix.first()

            val deferredResults = uris.map { uri ->
                async {
                    ImageSanitizer.sanitizeImage(
                        context = getApplication(),
                        uri = uri,
                        exportFormat = format,
                        quality = quality,
                        filenameSuffix = suffix
                    )
                }
            }

            val results = deferredResults.awaitAll()
            _imageResults.value = results

            val successCount = results.count { it is ImageSanitizeResult.Success }
            val errorCount = results.count { it is ImageSanitizeResult.Error }

            if (successCount > 0) {
                addHistory("Cleaned $successCount image${if (successCount > 1) "s" else ""}")
            }

            if (errorCount > 0) {
                _notifications.emit("Failed to process $errorCount image${if (errorCount > 1) "s" else ""}")
            } else if (successCount > 0) {
                _notifications.emit("Successfully processed $successCount image${if (successCount > 1) "s" else ""}")
            }

            _isProcessing.value = false
        }
    }

    fun clearImages() {
        if (_imageResults.value.isNotEmpty()) {
            _lastClearedImages.value = _imageResults.value
            _imageResults.value = emptyList()
        }
    }

    fun undoClearImages() {
        if (_lastClearedImages.value.isNotEmpty()) {
            _imageResults.value = _lastClearedImages.value
            _lastClearedImages.value = emptyList()
        }
    }

    private fun addHistory(item: String) {
        val current = _history.value.toMutableList()
        current.add(0, item)
        if (current.size > 3) {
            current.removeAt(current.size - 1)
        }
        _history.value = current
    }
}
