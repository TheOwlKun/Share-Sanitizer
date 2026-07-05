package org.sharesanitizer.app.ui

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.sharesanitizer.app.R
import org.sharesanitizer.app.ui.theme.ArtisticIcons
import org.sharesanitizer.app.viewmodel.SettingsViewModel

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
                title = { Text(stringResource(R.string.settings_title), fontFamily = FontFamily.Serif) },
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
        ) {
            val themePreference by viewModel.themePreference.collectAsState()
            var showThemeDialog by remember { mutableStateOf(false) }
            val themeOptions = listOf(
                stringResource(R.string.settings_theme_system),
                stringResource(R.string.settings_theme_light),
                stringResource(R.string.settings_theme_dark)
            )

            ListItem(
                headlineContent = { Text(stringResource(R.string.settings_theme)) },
                supportingContent = { Text(themeOptions[themePreference]) },
                modifier = Modifier.clickable { showThemeDialog = true }
            )

            if (showThemeDialog) {
                AlertDialog(
                    onDismissRequest = { showThemeDialog = false },
                    title = { Text(stringResource(R.string.settings_theme)) },
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
                        TextButton(onClick = { showThemeDialog = false }) { Text(stringResource(R.string.settings_cancel)) }
                    }
                )
            }

            HorizontalDivider(Modifier.padding(vertical = 8.dp))
            SettingsCategory(stringResource(R.string.settings_text_category))

            ListItem(
                headlineContent = { Text(stringResource(R.string.settings_trim_whitespace)) },
                supportingContent = { Text(stringResource(R.string.settings_trim_whitespace_desc)) },
                trailingContent = {
                    Switch(checked = trimWhitespace, onCheckedChange = { viewModel.setTrimWhitespace(it) })
                }
            )

            ListItem(
                headlineContent = { Text(stringResource(R.string.settings_collapse_lines)) },
                supportingContent = { Text(stringResource(R.string.settings_collapse_lines_desc)) },
                trailingContent = {
                    Switch(checked = collapseLines, onCheckedChange = { viewModel.setCollapseLines(it) })
                }
            )

            val stripInvisibleChars by viewModel.stripInvisibleChars.collectAsState()
            ListItem(
                headlineContent = { Text(stringResource(R.string.settings_strip_invisible)) },
                supportingContent = { Text(stringResource(R.string.settings_strip_invisible_desc)) },
                trailingContent = {
                    Switch(checked = stripInvisibleChars, onCheckedChange = { viewModel.setStripInvisibleChars(it) })
                }
            )

            val unwrapRedirects by viewModel.unwrapRedirects.collectAsState()
            ListItem(
                headlineContent = { Text(stringResource(R.string.settings_unwrap_redirects)) },
                supportingContent = { Text(stringResource(R.string.settings_unwrap_redirects_desc)) },
                trailingContent = {
                    Switch(checked = unwrapRedirects, onCheckedChange = { viewModel.setUnwrapRedirects(it) })
                }
            )

            HorizontalDivider(Modifier.padding(vertical = 8.dp))
            SettingsCategory(stringResource(R.string.settings_custom_params_category))

            val customTrackingParams by viewModel.customTrackingParams.collectAsState()
            var showAddParamDialog by remember { mutableStateOf(false) }
            val context = LocalContext.current

            val filePickerLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.GetContent()
            ) { uri ->
                uri?.let {
                    viewModel.importFilterList(it) { count ->
                        Toast.makeText(context, context.getString(R.string.settings_import_result, count), Toast.LENGTH_SHORT).show()
                    }
                }
            }

            ListItem(
                headlineContent = { Text(stringResource(R.string.settings_add_param)) },
                supportingContent = { Text(stringResource(R.string.settings_add_param_desc)) },
                modifier = Modifier.clickable { showAddParamDialog = true }
            )

            ListItem(
                headlineContent = { Text(stringResource(R.string.settings_import_filter)) },
                supportingContent = { Text(stringResource(R.string.settings_import_filter_desc)) },
                modifier = Modifier.clickable { filePickerLauncher.launch("text/plain") }
            )

            if (customTrackingParams.isNotEmpty()) {
                customTrackingParams.forEach { param ->
                    ListItem(
                        headlineContent = { Text(param, style = MaterialTheme.typography.bodyMedium) },
                        trailingContent = {
                            IconButton(onClick = { viewModel.removeCustomParam(param) }) {
                                Icon(Icons.Default.Close, contentDescription = stringResource(R.string.settings_remove))
                            }
                        }
                    )
                }
            }

            if (showAddParamDialog) {
                var newParam by remember { mutableStateOf("") }
                AlertDialog(
                    onDismissRequest = { showAddParamDialog = false },
                    title = { Text(stringResource(R.string.settings_add_param_title)) },
                    text = {
                        OutlinedTextField(
                            value = newParam,
                            onValueChange = { newParam = it },
                            label = { Text(stringResource(R.string.settings_add_param_label)) },
                            singleLine = true
                        )
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            viewModel.addCustomParam(newParam)
                            showAddParamDialog = false
                        }) { Text(stringResource(R.string.settings_add_param_confirm)) }
                    },
                    dismissButton = {
                        TextButton(onClick = { showAddParamDialog = false }) { Text(stringResource(R.string.settings_cancel)) }
                    }
                )
            }

            HorizontalDivider(Modifier.padding(vertical = 8.dp))
            SettingsCategory(stringResource(R.string.settings_image_category))

            var showFormatDialog by remember { mutableStateOf(false) }
            ListItem(
                headlineContent = { Text(stringResource(R.string.settings_export_format)) },
                supportingContent = { Text(exportFormat) },
                modifier = Modifier.clickable { showFormatDialog = true }
            )

            if (showFormatDialog) {
                AlertDialog(
                    onDismissRequest = { showFormatDialog = false },
                    title = { Text(stringResource(R.string.settings_export_format)) },
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
                        TextButton(onClick = { showFormatDialog = false }) { Text(stringResource(R.string.settings_cancel)) }
                    }
                )
            }

            ListItem(
                headlineContent = { Text(stringResource(R.string.settings_image_quality)) },
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
                headlineContent = { Text(stringResource(R.string.settings_filename_suffix)) },
                supportingContent = { Text(filenameSuffix) },
                modifier = Modifier.clickable { showSuffixDialog = true }
            )

            if (showSuffixDialog) {
                var tempSuffix by remember { mutableStateOf(filenameSuffix) }
                AlertDialog(
                    onDismissRequest = { showSuffixDialog = false },
                    title = { Text(stringResource(R.string.settings_filename_suffix)) },
                    text = {
                        OutlinedTextField(
                            value = tempSuffix,
                            onValueChange = { tempSuffix = it },
                            label = { Text(stringResource(R.string.settings_filename_suffix_label)) },
                            singleLine = true
                        )
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            viewModel.setFilenameSuffix(tempSuffix)
                            showSuffixDialog = false
                        }) { Text(stringResource(R.string.settings_filename_suffix_save)) }
                    },
                    dismissButton = {
                        TextButton(onClick = { showSuffixDialog = false }) { Text(stringResource(R.string.settings_cancel)) }
                    }
                )
            }

            ListItem(
                headlineContent = { Text(stringResource(R.string.settings_auto_delete_cache)) },
                supportingContent = { Text(stringResource(R.string.settings_auto_delete_cache_desc)) },
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
