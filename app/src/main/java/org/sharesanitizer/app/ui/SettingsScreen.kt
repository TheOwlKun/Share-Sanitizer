package org.sharesanitizer.app.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.sharesanitizer.app.viewmodel.SettingsViewModel
import kotlinx.coroutines.launch

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily

import org.sharesanitizer.app.ui.theme.ArtisticIcons

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onBack: () -> Unit
) {
    val trimWhitespace by viewModel.trimWhitespace.collectAsState()
    val collapseLines by viewModel.collapseLines.collectAsState()
    val exportFormat by viewModel.exportFormat.collectAsState()
    val imageQuality by viewModel.imageQuality.collectAsState()
    val filenameSuffix by viewModel.filenameSuffix.collectAsState()
    val autoDeleteCache by viewModel.autoDeleteCache.collectAsState()

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                title = { Text("Settings", fontFamily = FontFamily.Serif) },
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
        ) {
            val themePreference by viewModel.themePreference.collectAsState()
            var showThemeDialog by remember { mutableStateOf(false) }
            val themeOptions = listOf("System Default", "Light", "Dark")
            
            ListItem(
                headlineContent = { Text("App Theme") },
                supportingContent = { Text(themeOptions[themePreference]) },
                modifier = Modifier.clickable { showThemeDialog = true }
            )
            
            if (showThemeDialog) {
                AlertDialog(
                    onDismissRequest = { showThemeDialog = false },
                    title = { Text("App Theme") },
                    text = {
                        Column(Modifier.selectableGroup()) {
                            themeOptions.forEachIndexed { index, option ->
                                Row(
                                    Modifier
                                        .fillMaxWidth()
                                        .height(48.dp)
                                        .clickable {
                                            viewModel.setThemePreference(index)
                                            showThemeDialog = false
                                        },
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = (index == themePreference),
                                        onClick = null
                                    )
                                    Spacer(Modifier.width(16.dp))
                                    Text(option)
                                }
                            }
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = { showThemeDialog = false }) { Text("Cancel") }
                    }
                )
            }
            
            Divider(Modifier.padding(vertical = 8.dp))
            SettingsCategory("Text Sanitization")
            
            ListItem(
                headlineContent = { Text("Trim Whitespace") },
                supportingContent = { Text("Remove leading and trailing empty space from shared text") },
                trailingContent = {
                    Switch(checked = trimWhitespace, onCheckedChange = { viewModel.setTrimWhitespace(it) })
                }
            )
            
            ListItem(
                headlineContent = { Text("Collapse Blank Lines") },
                supportingContent = { Text("Reduce excessive blank lines to a maximum of two") },
                trailingContent = {
                    Switch(checked = collapseLines, onCheckedChange = { viewModel.setCollapseLines(it) })
                }
            )
            
            
            Divider(Modifier.padding(vertical = 8.dp))
            SettingsCategory("Custom Tracking Parameters")
            
            val customTrackingParams by viewModel.customTrackingParams.collectAsState()
            var showAddParamDialog by remember { mutableStateOf(false) }
            val context = LocalContext.current
            
            val filePickerLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.GetContent()
            ) { uri ->
                uri?.let {
                    viewModel.importFilterList(it) { count ->
                        Toast.makeText(context, "Imported $count parameters", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            
            ListItem(
                headlineContent = { Text("Add Custom Parameter") },
                supportingContent = { Text("Add your own tracking parameters to be stripped from URLs") },
                modifier = Modifier.clickable { showAddParamDialog = true }
            )
            
            ListItem(
                headlineContent = { Text("Import AdGuard Filter List") },
                supportingContent = { Text("Import a .txt file (like 17.txt) to update blocklist offline") },
                modifier = Modifier.clickable { filePickerLauncher.launch("text/plain") }
            )
            
            if (customTrackingParams.isNotEmpty()) {
                customTrackingParams.forEach { param ->
                    ListItem(
                        headlineContent = { Text(param, style = MaterialTheme.typography.bodyMedium) },
                        trailingContent = {
                            IconButton(onClick = { viewModel.removeCustomParam(param) }) {
                                Icon(Icons.Default.Close, contentDescription = "Remove")
                            }
                        }
                    )
                }
            }
            
            if (showAddParamDialog) {
                var newParam by remember { mutableStateOf("") }
                AlertDialog(
                    onDismissRequest = { showAddParamDialog = false },
                    title = { Text("Add Parameter") },
                    text = {
                        OutlinedTextField(
                            value = newParam,
                            onValueChange = { newParam = it },
                            label = { Text("Parameter name (e.g. ref)") },
                            singleLine = true
                        )
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            viewModel.addCustomParam(newParam)
                            showAddParamDialog = false
                        }) { Text("Add") }
                    },
                    dismissButton = {
                        TextButton(onClick = { showAddParamDialog = false }) { Text("Cancel") }
                    }
                )
            }
            
            Divider(Modifier.padding(vertical = 8.dp))
            SettingsCategory("Image Sanitization")
            
            var showFormatDialog by remember { mutableStateOf(false) }
            ListItem(
                headlineContent = { Text("Export Format") },
                supportingContent = { Text(exportFormat) },
                modifier = Modifier.clickable { showFormatDialog = true }
            )
            
            if (showFormatDialog) {
                AlertDialog(
                    onDismissRequest = { showFormatDialog = false },
                    title = { Text("Export Format") },
                    text = {
                        Column(Modifier.selectableGroup()) {
                            val options = listOf("ORIGINAL", "JPEG", "PNG", "WEBP")
                            options.forEach { option ->
                                Row(
                                    Modifier
                                        .fillMaxWidth()
                                        .height(48.dp)
                                        .clickable {
                                            viewModel.setExportFormat(option)
                                            showFormatDialog = false
                                        },
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = (option == exportFormat),
                                        onClick = null
                                    )
                                    Spacer(Modifier.width(16.dp))
                                    Text(option)
                                }
                            }
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = { showFormatDialog = false }) { Text("Cancel") }
                    }
                )
            }
            
            ListItem(
                headlineContent = { Text("Image Quality (JPEG/WebP)") },
                supportingContent = { 
                    Column {
                        Text("$imageQuality%")
                        Slider(
                            value = imageQuality.toFloat(),
                            onValueChange = { viewModel.setImageQuality(it.toInt()) },
                            valueRange = 10f..100f
                        )
                    }
                }
            )
            
            var showSuffixDialog by remember { mutableStateOf(false) }
            ListItem(
                headlineContent = { Text("Filename Suffix") },
                supportingContent = { Text(filenameSuffix) },
                modifier = Modifier.clickable { showSuffixDialog = true }
            )
            
            if (showSuffixDialog) {
                var tempSuffix by remember { mutableStateOf(filenameSuffix) }
                AlertDialog(
                    onDismissRequest = { showSuffixDialog = false },
                    title = { Text("Filename Suffix") },
                    text = {
                        OutlinedTextField(
                            value = tempSuffix,
                            onValueChange = { tempSuffix = it },
                            label = { Text("Suffix") },
                            singleLine = true
                        )
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            viewModel.setFilenameSuffix(tempSuffix)
                            showSuffixDialog = false
                        }) { Text("Save") }
                    },
                    dismissButton = {
                        TextButton(onClick = { showSuffixDialog = false }) { Text("Cancel") }
                    }
                )
            }

            ListItem(
                headlineContent = { Text("Auto-Delete Cache") },
                supportingContent = { Text("Automatically delete sanitized images from cache when the app starts") },
                trailingContent = {
                    Switch(checked = autoDeleteCache, onCheckedChange = { viewModel.setAutoDeleteCache(it) })
                }
            )
        }
    }
}

@Composable
fun SettingsCategory(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp, end = 16.dp),
        fontWeight = FontWeight.Bold
    )
}



