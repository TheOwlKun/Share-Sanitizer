package org.sharesanitizer.app.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily

import org.sharesanitizer.app.BuildConfig
import org.sharesanitizer.app.ui.theme.ArtisticIcons

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    
    // Entrance animation
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                title = { Text("About & Privacy", fontFamily = FontFamily.Serif) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(ArtisticIcons.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(animationSpec = tween(600)) + slideInVertically(
                    initialOffsetY = { -20 },
                    animationSpec = tween(600, easing = FastOutSlowInEasing)
                )
            ) {
                Column {
                    Text("Share Sanitizer", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif)
                    Text("Version ${BuildConfig.VERSION_NAME}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            
            HorizontalDivider(Modifier.padding(vertical = 8.dp))

            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(animationSpec = tween(600, delayMillis = 100)) + slideInVertically(
                    initialOffsetY = { 30 },
                    animationSpec = tween(600, delayMillis = 100, easing = FastOutSlowInEasing)
                )
            ) {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(Modifier.padding(20.dp)) {
                        Text("Privacy First", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "This app is fully offline. It has no network permission, no analytics, no tracking, and no crash reporting. " +
                            "All text and image sanitization happens entirely on your device.",
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
            
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(animationSpec = tween(600, delayMillis = 200)) + slideInVertically(
                    initialOffsetY = { 30 },
                    animationSpec = tween(600, delayMillis = 200, easing = FastOutSlowInEasing)
                )
            ) {
                Card(shape = RoundedCornerShape(20.dp)) {
                    Column(Modifier.padding(20.dp)) {
                        Text("How Images are Sanitized", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "When you sanitize an image, Share Sanitizer reads the raw pixel data and saves it to a brand new file. " +
                            "This ensures that EXIF metadata (like GPS location, camera model, date/time taken) is completely dropped. " +
                            "Your original files are never overwritten."
                        )
                    }
                }
            }
            
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(animationSpec = tween(600, delayMillis = 300)) + slideInVertically(
                    initialOffsetY = { 30 },
                    animationSpec = tween(600, delayMillis = 300, easing = FastOutSlowInEasing)
                )
            ) {
                Card(shape = RoundedCornerShape(20.dp)) {
                    Column(Modifier.padding(20.dp)) {
                        Text("How Text is Sanitized", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Share Sanitizer looks for URLs in your shared text. It removes common tracking parameters and applies bundled offline Community URL Rules " +
                            "for domain-specific cleanup, while preserving functional link parameters whenever possible."
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Community URL Rules are based on an offline snapshot of the ClearURLs Rules database, licensed under LGPL-3.0.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(animationSpec = tween(600, delayMillis = 400)) + slideInVertically(
                    initialOffsetY = { 30 },
                    animationSpec = tween(600, delayMillis = 400, easing = FastOutSlowInEasing)
                )
            ) {
                Card(shape = RoundedCornerShape(20.dp)) {
                    Column(Modifier.padding(20.dp)) {
                        Text("Open Source", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "This application is free and open-source software, licensed under the GNU General Public License v3.0 (GPL-3.0). " +
                            "You can review the source code to verify these privacy claims."
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(
                            "View Source Code →",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.primary,
                                textDecoration = TextDecoration.Underline,
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.clickable {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/TheOwlKun/share-sanitizer"))
                                context.startActivity(intent)
                            }
                        )
                    }
                }
            }

            HorizontalDivider(Modifier.padding(vertical = 8.dp))

            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(animationSpec = tween(600, delayMillis = 500)) + slideInVertically(
                    initialOffsetY = { 30 },
                    animationSpec = tween(600, delayMillis = 500, easing = FastOutSlowInEasing)
                )
            ) {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
                ) {
                    Column(Modifier.padding(20.dp)) {
                        Text("Support the Developer", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onTertiaryContainer)
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "If you find Share Sanitizer useful, consider buying me a coffee! " +
                            "Your support helps keep this project alive and free for everyone.",
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                        Spacer(Modifier.height(12.dp))
                        Button(
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://buymeacoffee.com/theowlkun"))
                                context.startActivity(intent)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.tertiary,
                                contentColor = MaterialTheme.colorScheme.onTertiary
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("☕  Buy Me a Coffee", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(animationSpec = tween(600, delayMillis = 600)) + slideInVertically(
                    initialOffsetY = { 30 },
                    animationSpec = tween(600, delayMillis = 600, easing = FastOutSlowInEasing)
                )
            ) {
                Column {
                    Text("Developer", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.height(8.dp))
                    Text("Built by TheOwlKun")
                    Text(
                        "github.com/TheOwlKun",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.primary,
                            textDecoration = TextDecoration.Underline
                        ),
                        modifier = Modifier.clickable {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/TheOwlKun"))
                            context.startActivity(intent)
                        }
                    )

                    Spacer(Modifier.height(16.dp))

                    Text(
                        "Made with care for privacy-conscious users.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(Modifier.height(4.dp))
                    
                    Text(
                        "License: GPL-3.0",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
