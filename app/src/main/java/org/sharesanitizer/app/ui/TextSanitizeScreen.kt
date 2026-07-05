package org.sharesanitizer.app.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.sharesanitizer.app.R
import org.sharesanitizer.app.ui.theme.ArtisticIcons
import org.sharesanitizer.app.viewmodel.ShareViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextSanitizeScreen(
    viewModel: ShareViewModel,
    onBack: () -> Unit
) {
    val result by viewModel.textResult.collectAsState()
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current

    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(result) {
        if (result != null) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            visible = true
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                title = { Text(stringResource(R.string.text_title), fontFamily = FontFamily.Serif) },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.clearText()
                        onBack()
                    }) {
                        Icon(ArtisticIcons.ArrowBack, contentDescription = stringResource(R.string.action_back))
                    }
                }
            )
        },
        bottomBar = {
            if (result != null) {
                BottomAppBar(containerColor = Color.Transparent) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        AnimatedVisibility(
                            visible = visible,
                            enter = fadeIn(animationSpec = tween(500, delayMillis = 100)) + slideInVertically(initialOffsetY = { 50 }, animationSpec = tween(500, delayMillis = 100)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Button(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    clipboard.setPrimaryClip(ClipData.newPlainText("Sanitized Text", result!!.cleaned))
                                    Toast.makeText(context, context.getString(R.string.text_copied_toast), Toast.LENGTH_SHORT).show()
                                }
                            ) {
                                Icon(ArtisticIcons.ContentCopy, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text(stringResource(R.string.text_copy))
                            }
                        }
                        Spacer(Modifier.width(16.dp))
                        AnimatedVisibility(
                            visible = visible,
                            enter = fadeIn(animationSpec = tween(500, delayMillis = 250)) + slideInVertically(initialOffsetY = { 50 }, animationSpec = tween(500, delayMillis = 250)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Button(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_SEND).apply {
                                        type = "text/plain"
                                        putExtra(Intent.EXTRA_TEXT, result!!.cleaned)
                                    }
                                    context.startActivity(Intent.createChooser(intent, context.getString(R.string.text_share_chooser)))
                                }
                            ) {
                                Icon(ArtisticIcons.Share, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text(stringResource(R.string.text_share))
                            }
                        }
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (result == null) {
                Text(stringResource(R.string.text_no_input), style = MaterialTheme.typography.bodyLarge)
            } else {
                val res = result!!

                if (res.removed.isNotEmpty()) {
                    val totalParams = res.removed.sumOf { it.params.size }

                    AnimatedVisibility(
                        visible = visible,
                        enter = fadeIn(animationSpec = tween(500)) + slideInVertically(
                            initialOffsetY = { 30 },
                            animationSpec = tween(500, easing = FastOutSlowInEasing)
                        )
                    ) {
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
                        ) {
                            Column(Modifier.padding(20.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .background(
                                                MaterialTheme.colorScheme.tertiary,
                                                RoundedCornerShape(12.dp)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            "$totalParams",
                                            color = MaterialTheme.colorScheme.onTertiary,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp
                                        )
                                    }
                                    Column {
                                        Text(
                                            stringResource(R.string.text_tracking_removed),
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            stringResource(R.string.text_from_urls, res.removed.size, if (res.removed.size > 1) "s" else ""),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.7f)
                                        )
                                    }
                                }
                                if (res.invisibleCharsRemoved > 0) {
                                    Spacer(Modifier.height(8.dp))
                                    Text(
                                        stringResource(R.string.text_invisible_stripped, res.invisibleCharsRemoved),
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f)
                                    )
                                }
                                Spacer(Modifier.height(12.dp))
                                HorizontalDivider(color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.15f))
                                Spacer(Modifier.height(12.dp))
                                res.removed.forEach { removed ->
                                    Text(
                                        "URL: ${removed.url.take(50)}${if (removed.url.length > 50) "…" else ""}",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        "Removed: ${removed.params.joinToString()}",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                    Spacer(Modifier.height(8.dp))
                                }
                            }
                        }
                    }
                } else {
                    AnimatedVisibility(
                        visible = visible,
                        enter = fadeIn(animationSpec = tween(500))
                    ) {
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                        ) {
                            Column(Modifier.padding(20.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Icon(
                                        ArtisticIcons.Check,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Text(
                                        stringResource(R.string.text_already_clean),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                if (res.invisibleCharsRemoved > 0) {
                                    Spacer(Modifier.height(8.dp))
                                    Text(
                                        stringResource(R.string.text_invisible_stripped, res.invisibleCharsRemoved),
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                                    )
                                }
                            }
                        }
                    }
                }

                if (res.shortenedUrls.isNotEmpty()) {
                    AnimatedVisibility(
                        visible = visible,
                        enter = fadeIn(animationSpec = tween(500, delayMillis = 150)) + slideInVertically(
                            initialOffsetY = { 30 },
                            animationSpec = tween(500, delayMillis = 150, easing = FastOutSlowInEasing)
                        )
                    ) {
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                        ) {
                            Column(Modifier.padding(20.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Icon(
                                        ArtisticIcons.Shield,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Text(
                                        stringResource(R.string.text_shortened_title),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                }
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    stringResource(R.string.text_shortened_warning, res.shortenedUrls.size, if (res.shortenedUrls.size > 1) "s" else ""),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.8f)
                                )
                                Spacer(Modifier.height(8.dp))
                                res.shortenedUrls.forEach { shortUrl ->
                                    Text(
                                        shortUrl,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }
                }

                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(animationSpec = tween(500, delayMillis = 200)) + slideInVertically(
                        initialOffsetY = { 30 },
                        animationSpec = tween(500, delayMillis = 200, easing = FastOutSlowInEasing)
                    )
                ) {
                    Card(
                        Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Column(Modifier.padding(20.dp)) {
                            Text(stringResource(R.string.text_cleaned), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(8.dp))
                            Text(res.cleaned, style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }

                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(animationSpec = tween(500, delayMillis = 400)) + slideInVertically(
                        initialOffsetY = { 30 },
                        animationSpec = tween(500, delayMillis = 400, easing = FastOutSlowInEasing)
                    )
                ) {
                    Card(
                        Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Column(Modifier.padding(20.dp)) {
                            Text(stringResource(R.string.text_original), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(8.dp))
                            Text(res.original, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }

                Spacer(Modifier.height(80.dp))
            }
        }
    }
}
