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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import org.sharesanitizer.app.BuildConfig
import org.sharesanitizer.app.R
import org.sharesanitizer.app.ui.theme.ArtisticIcons

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current

    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                title = { Text(stringResource(R.string.about_title), fontFamily = FontFamily.Serif) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(ArtisticIcons.ArrowBack, contentDescription = stringResource(R.string.action_back))
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
                    Text(stringResource(R.string.app_name), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif)
                    Text(stringResource(R.string.about_version, BuildConfig.VERSION_NAME), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
                        Text(stringResource(R.string.about_privacy_title), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        Spacer(Modifier.height(8.dp))
                        Text(
                            stringResource(R.string.about_privacy_body),
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
                        Text(stringResource(R.string.about_image_title), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.height(8.dp))
                        Text(stringResource(R.string.about_image_body))
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
                        Text(stringResource(R.string.about_text_title), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.height(8.dp))
                        Text(stringResource(R.string.about_text_body))
                        Spacer(Modifier.height(8.dp))
                        Text(
                            stringResource(R.string.about_text_rules_note),
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
                        Text(stringResource(R.string.about_open_source_title), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.height(8.dp))
                        Text(stringResource(R.string.about_open_source_body))
                        Spacer(Modifier.height(12.dp))
                        Text(
                            stringResource(R.string.about_view_source),
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
                        Text(stringResource(R.string.about_support_title), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onTertiaryContainer)
                        Spacer(Modifier.height(8.dp))
                        Text(
                            stringResource(R.string.about_support_body),
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
                            Text(stringResource(R.string.about_buy_coffee), fontWeight = FontWeight.Bold)
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
                    Text(stringResource(R.string.about_developer), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.height(8.dp))
                    Text(stringResource(R.string.about_built_by))
                    Text(
                        stringResource(R.string.about_github),
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
                        stringResource(R.string.about_tagline),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(Modifier.height(4.dp))

                    Text(
                        stringResource(R.string.about_license),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
