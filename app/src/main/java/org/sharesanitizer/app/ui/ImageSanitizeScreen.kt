package org.sharesanitizer.app.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import org.sharesanitizer.app.R
import org.sharesanitizer.app.sanitizer.ImageSanitizeResult
import org.sharesanitizer.app.ui.theme.ArtisticIcons
import org.sharesanitizer.app.viewmodel.ShareViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageSanitizeScreen(
    viewModel: ShareViewModel,
    onBack: () -> Unit
) {
    val results by viewModel.imageResults.collectAsState()
    val isProcessing by viewModel.isProcessing.collectAsState()
    val canUndo by viewModel.canUndoImages.collectAsState()
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current

    var showContent by remember { mutableStateOf(false) }
    LaunchedEffect(isProcessing) {
        if (!isProcessing && results.isNotEmpty()) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            showContent = true
        }
    }

    val saveLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("image/*")) { uri ->
        if (uri != null) {
            val successfulResults = results.filterIsInstance<ImageSanitizeResult.Success>()
            if (successfulResults.isNotEmpty()) {
                val result = successfulResults.first()
                try {
                    context.contentResolver.openOutputStream(uri)?.use { out ->
                        result.outputFile.inputStream().use { input ->
                            input.copyTo(out)
                        }
                    }
                } catch (_: Exception) { }
            }
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                title = { Text(stringResource(R.string.image_title), fontFamily = FontFamily.Serif) },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.clearImages()
                        onBack()
                    }) {
                        Icon(ArtisticIcons.ArrowBack, contentDescription = stringResource(R.string.action_back))
                    }
                },
                actions = {
                    if (results.isNotEmpty()) {
                        IconButton(onClick = { viewModel.clearImages() }) {
                            Icon(ArtisticIcons.Delete, contentDescription = stringResource(R.string.image_clear))
                        }
                    } else if (canUndo) {
                        IconButton(onClick = { viewModel.undoClearImages() }) {
                            Icon(ArtisticIcons.Undo, contentDescription = stringResource(R.string.image_undo))
                        }
                    }
                }
            )
        },
        bottomBar = {
            if (!isProcessing && results.isNotEmpty()) {
                val successfulResults = results.filterIsInstance<ImageSanitizeResult.Success>()
                if (successfulResults.isNotEmpty()) {
                    BottomAppBar(containerColor = Color.Transparent) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            if (successfulResults.size == 1) {
                                AnimatedVisibility(
                                    visible = showContent,
                                    enter = fadeIn(animationSpec = tween(500, delayMillis = 100)) + slideInVertically(initialOffsetY = { 50 }, animationSpec = tween(500, delayMillis = 100)),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Button(
                                        onClick = {
                                            val result = successfulResults.first()
                                            saveLauncher.launch(result.outputFile.name)
                                        }
                                    ) {
                                        Icon(ArtisticIcons.Save, contentDescription = null)
                                        Spacer(Modifier.width(8.dp))
                                        Text(stringResource(R.string.image_save))
                                    }
                                }
                                Spacer(Modifier.width(16.dp))
                            }
                            AnimatedVisibility(
                                visible = showContent,
                                enter = fadeIn(animationSpec = tween(500, delayMillis = 250)) + slideInVertically(initialOffsetY = { 50 }, animationSpec = tween(500, delayMillis = 250)),
                                modifier = Modifier.weight(1f)
                            ) {
                                Button(
                                    onClick = { shareImages(context, successfulResults) }
                                ) {
                                    Icon(ArtisticIcons.Share, contentDescription = null)
                                    Spacer(Modifier.width(8.dp))
                                    Text(stringResource(R.string.image_share_all))
                                }
                            }
                        }
                    }
                }
            }
        }
    ) { padding ->
        if (isProcessing) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                val infiniteTransition = rememberInfiniteTransition(label = "processing")
                val scale by infiniteTransition.animateFloat(
                    initialValue = 0.8f,
                    targetValue = 1.3f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(800, easing = FastOutSlowInEasing),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "processing_scale"
                )
                val alpha by infiniteTransition.animateFloat(
                    initialValue = 0.5f,
                    targetValue = 1f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(800, easing = FastOutSlowInEasing),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "processing_alpha"
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        ArtisticIcons.Image,
                        contentDescription = stringResource(R.string.image_processing),
                        modifier = Modifier.size(72.dp).scale(scale),
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = alpha)
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        stringResource(R.string.image_stripping),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else if (results.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text(stringResource(R.string.image_no_selection))
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    AnimatedVisibility(
                        visible = showContent,
                        enter = fadeIn(animationSpec = tween(500)) + slideInVertically(
                            initialOffsetY = { 30 },
                            animationSpec = tween(500, easing = FastOutSlowInEasing)
                        )
                    ) {
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
                        ) {
                            Row(
                                Modifier.padding(20.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(
                                    ArtisticIcons.Check,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.tertiary,
                                    modifier = Modifier.size(28.dp)
                                )
                                Column {
                                    Text(
                                        stringResource(R.string.image_metadata_stripped),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        stringResource(R.string.image_metadata_body),
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    }
                }

                itemsIndexed(results) { index, result ->
                    AnimatedVisibility(
                        visible = showContent,
                        enter = fadeIn(animationSpec = tween(500, delayMillis = 150 * (index + 1))) + slideInVertically(
                            initialOffsetY = { 40 },
                            animationSpec = tween(500, delayMillis = 150 * (index + 1), easing = FastOutSlowInEasing)
                        )
                    ) {
                        when (result) {
                            is ImageSanitizeResult.Success -> {
                                Card(
                                    Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(20.dp)
                                ) {
                                    Column(Modifier.padding(20.dp)) {
                                        Text(
                                            result.originalName,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(Modifier.height(12.dp))

                                        Row(
                                            Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Column {
                                                Text(stringResource(R.string.image_original), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                Text(formatSize(result.originalSize), style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                                            }
                                            Column {
                                                Text(stringResource(R.string.image_cleaned), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                Text(formatSize(result.outputSize), style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                                            }
                                            Column(horizontalAlignment = Alignment.End) {
                                                Text(stringResource(R.string.image_change), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                val changePercent = if (result.originalSize > 0) {
                                                    ((result.outputSize.toDouble() - result.originalSize.toDouble()) / result.originalSize.toDouble() * 100).toInt()
                                                } else 0
                                                val changeText = if (changePercent <= 0) "${changePercent}%" else "+${changePercent}%"
                                                val changeColor = if (changePercent <= 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                                                Text(
                                                    changeText,
                                                    style = MaterialTheme.typography.bodyLarge,
                                                    fontWeight = FontWeight.Bold,
                                                    color = changeColor
                                                )
                                            }
                                        }

                                        Spacer(Modifier.height(8.dp))
                                        HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                                        Spacer(Modifier.height(8.dp))

                                        Row(
                                            Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                stringResource(R.string.image_output, result.outputFile.name),
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            Text(
                                                result.format.name,
                                                style = MaterialTheme.typography.bodySmall,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }
                                }
                            }
                            is ImageSanitizeResult.Error -> {
                                Card(
                                    Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(20.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                                ) {
                                    Column(Modifier.padding(20.dp)) {
                                        Text(stringResource(R.string.image_error_title), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onErrorContainer)
                                        Spacer(Modifier.height(8.dp))
                                        Text(result.error, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onErrorContainer)
                                    }
                                }
                            }
                        }
                    }
                }

                item { Spacer(Modifier.height(80.dp)) }
            }
        }
    }
}

private fun formatSize(bytes: Long): String {
    if (bytes <= 0) return "0 B"
    val units = arrayOf("B", "KB", "MB", "GB", "TB")
    val digitGroups = (Math.log10(bytes.toDouble()) / Math.log10(1024.0)).toInt()
    return String.format("%.1f %s", bytes / Math.pow(1024.0, digitGroups.toDouble()), units[digitGroups])
}

private fun shareImages(context: Context, results: List<ImageSanitizeResult.Success>) {
    val uris = results.map {
        FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", it.outputFile)
    }

    if (uris.isEmpty()) return

    if (uris.size == 1) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/*"
            putExtra(Intent.EXTRA_STREAM, uris.first())
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, context.getString(R.string.image_share_single_chooser)))
    } else {
        val intent = Intent(Intent.ACTION_SEND_MULTIPLE).apply {
            type = "image/*"
            putParcelableArrayListExtra(Intent.EXTRA_STREAM, ArrayList(uris))
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, context.getString(R.string.image_share_multi_chooser)))
    }
}
