package org.sharesanitizer.app.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
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
fun HomeScreen(
    onNavigateToText: () -> Unit,
    onNavigateToImage: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToAbout: () -> Unit,
    shareViewModel: ShareViewModel
) {
    var showPermissionsDialog by remember { mutableStateOf(false) }
    var showInstallerDialog by remember { mutableStateOf(false) }

    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    val infiniteTransition = rememberInfiniteTransition(label = "shield_pulse")
    val shieldScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shield_scale"
    )

    val context = LocalContext.current
    LaunchedEffect(Unit) {
        val installer = try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                context.packageManager.getInstallSourceInfo(context.packageName).installingPackageName
            } else {
                @Suppress("DEPRECATION")
                context.packageManager.getInstallerPackageName(context.packageName)
            }
        } catch (_: Exception) { null }

        if (installer == "com.android.vending") {
            showInstallerDialog = true
        }
    }

    val imageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            shareViewModel.sanitizeImages(uris)
            onNavigateToImage()
        }
    }

    var showPasteDialog by remember { mutableStateOf(false) }

    if (showPasteDialog) {
        var pastedText by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showPasteDialog = false },
            title = { Text(stringResource(R.string.paste_title), fontFamily = FontFamily.Serif) },
            text = {
                OutlinedTextField(
                    value = pastedText,
                    onValueChange = { pastedText = it },
                    label = { Text(stringResource(R.string.paste_label)) },
                    modifier = Modifier.fillMaxWidth().imePadding(),
                    minLines = 3
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (pastedText.isNotBlank()) {
                        shareViewModel.sanitizeText(pastedText)
                        showPasteDialog = false
                        onNavigateToText()
                    }
                }) {
                    Text(stringResource(R.string.paste_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { showPasteDialog = false }) { Text(stringResource(R.string.paste_cancel)) }
            },
            properties = androidx.compose.ui.window.DialogProperties(
                decorFitsSystemWindows = false
            )
        )
    }

    if (showPermissionsDialog) {
        AlertDialog(
            onDismissRequest = { showPermissionsDialog = false },
            title = { Text(stringResource(R.string.home_permissions_title), fontFamily = FontFamily.Serif) },
            text = { Text(stringResource(R.string.home_permissions_body)) },
            confirmButton = {
                TextButton(onClick = { showPermissionsDialog = false }) { Text(stringResource(R.string.home_permissions_ok)) }
            }
        )
    }

    if (showInstallerDialog) {
        AlertDialog(
            onDismissRequest = { showInstallerDialog = false },
            title = { Text(stringResource(R.string.home_installer_title), fontFamily = FontFamily.Serif) },
            text = { Text(stringResource(R.string.home_installer_body)) },
            confirmButton = {
                TextButton(onClick = { showInstallerDialog = false }) { Text(stringResource(R.string.home_installer_ok)) }
            }
        )
    }

    Scaffold(
        containerColor = Color.Transparent,
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                tonalElevation = 0.dp,
                modifier = Modifier.height(80.dp)
            ) {
                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    icon = { Icon(ArtisticIcons.Home, contentDescription = null) },
                    label = { Text(stringResource(R.string.nav_home), fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                        selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        selectedTextColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToSettings,
                    icon = { Icon(ArtisticIcons.Settings, contentDescription = null) },
                    label = { Text(stringResource(R.string.nav_settings)) },
                    colors = NavigationBarItemDefaults.colors(
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToAbout,
                    icon = { Icon(ArtisticIcons.Info, contentDescription = null) },
                    label = { Text(stringResource(R.string.nav_about)) },
                    colors = NavigationBarItemDefaults.colors(
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .imePadding()
                .verticalScroll(rememberScrollState())
        ) {
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(animationSpec = tween(600)) + slideInVertically(
                    initialOffsetY = { -30 },
                    animationSpec = tween(600, easing = FastOutSlowInEasing)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        Text(
                            stringResource(R.string.home_title),
                            fontSize = 32.sp,
                            fontFamily = FontFamily.Serif,
                            lineHeight = 36.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .scale(shieldScale)
                            .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            ArtisticIcons.Shield,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }

            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(animationSpec = tween(600, delayMillis = 150)) + slideInVertically(
                    initialOffsetY = { 40 },
                    animationSpec = tween(600, delayMillis = 150, easing = FastOutSlowInEasing)
                )
            ) {
                Card(
                    onClick = { showPermissionsDialog = true },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .padding(bottom = 24.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.secondaryContainer)
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(MaterialTheme.colorScheme.surface, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                ArtisticIcons.Check,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                stringResource(R.string.home_privacy_title),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Text(
                                stringResource(R.string.home_privacy_subtitle),
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }

            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(animationSpec = tween(600, delayMillis = 300)) + slideInVertically(
                    initialOffsetY = { 60 },
                    animationSpec = tween(600, delayMillis = 300, easing = FastOutSlowInEasing)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        HomeActionCard(
                            title = stringResource(R.string.home_sanitize_urls_title),
                            subtitle = stringResource(R.string.home_sanitize_urls_subtitle),
                            icon = ArtisticIcons.Link,
                            onClick = { showPasteDialog = true }
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        HomeActionCard(
                            title = stringResource(R.string.home_clean_image_title),
                            subtitle = stringResource(R.string.home_clean_image_subtitle),
                            icon = ArtisticIcons.Image,
                            onClick = { imageLauncher.launch("image/*") }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(animationSpec = tween(600, delayMillis = 450)) + slideInVertically(
                    initialOffsetY = { 60 },
                    animationSpec = tween(600, delayMillis = 450, easing = FastOutSlowInEasing)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        HomeActionCard(
                            title = stringResource(R.string.home_batch_title),
                            subtitle = stringResource(R.string.home_batch_subtitle),
                            icon = ArtisticIcons.Collections,
                            onClick = { imageLauncher.launch("image/*") }
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        HomeActionCard(
                            title = stringResource(R.string.home_pick_files_title),
                            subtitle = stringResource(R.string.home_pick_files_subtitle),
                            icon = ArtisticIcons.Folder,
                            onClick = { imageLauncher.launch("image/*") }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            val history by shareViewModel.history.collectAsState()
            if (history.isNotEmpty()) {
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(animationSpec = tween(600, delayMillis = 600)) + slideInVertically(
                        initialOffsetY = { 40 },
                        animationSpec = tween(600, delayMillis = 600, easing = FastOutSlowInEasing)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                    ) {
                        Text(
                            stringResource(R.string.home_recent_history),
                            fontSize = 18.sp,
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                history.forEachIndexed { index, item ->
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(6.dp)
                                                .background(MaterialTheme.colorScheme.primary, CircleShape)
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(
                                            text = item,
                                            fontSize = 14.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontFamily = FontFamily.Serif
                                        )
                                    }
                                    if (index < history.size - 1) {
                                        HorizontalDivider(
                                            modifier = Modifier.padding(vertical = 8.dp),
                                            color = MaterialTheme.colorScheme.surfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(animationSpec = tween(600, delayMillis = 700)) + slideInVertically(
                    initialOffsetY = { 40 },
                    animationSpec = tween(600, delayMillis = 700, easing = FastOutSlowInEasing)
                )
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(ArtisticIcons.Share, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = stringResource(R.string.home_share_hint),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                lineHeight = 18.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                subtitle,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}
