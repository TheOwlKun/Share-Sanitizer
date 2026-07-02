package org.sharesanitizer.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import org.sharesanitizer.app.settings.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.net.Uri
import java.io.InputStreamReader
import java.io.BufferedReader
import java.util.regex.Pattern

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = SettingsRepository(application)

    val trimWhitespace = repository.trimWhitespace.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)
    val collapseLines = repository.collapseLines.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)
    val exportFormat = repository.exportFormat.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "ORIGINAL")
    val imageQuality = repository.imageQuality.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 90)
    val filenameSuffix = repository.filenameSuffix.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "_clean")
    val autoDeleteCache = repository.autoDeleteCache.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)
    val themePreference = repository.themePreference.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    val customTrackingParams = repository.customTrackingParams.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    fun setTrimWhitespace(value: Boolean) = viewModelScope.launch { repository.setTrimWhitespace(value) }
    fun setCollapseLines(value: Boolean) = viewModelScope.launch { repository.setCollapseLines(value) }
    fun setExportFormat(format: String) = viewModelScope.launch { repository.setExportFormat(format) }
    fun setImageQuality(quality: Int) = viewModelScope.launch { repository.setImageQuality(quality) }
    fun setFilenameSuffix(suffix: String) = viewModelScope.launch { repository.setFilenameSuffix(suffix) }
    fun setAutoDeleteCache(value: Boolean) = viewModelScope.launch { repository.setAutoDeleteCache(value) }
    fun setThemePreference(theme: Int) = viewModelScope.launch { repository.setThemePreference(theme) }
    fun addCustomParam(param: String) {
        if (param.isNotBlank()) viewModelScope.launch { repository.addCustomTrackingParam(param) }
    }
    fun removeCustomParam(param: String) = viewModelScope.launch { repository.removeCustomTrackingParam(param) }

    fun importFilterList(uri: Uri, onResult: (Int) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            var addedCount = 0
            try {
                getApplication<Application>().contentResolver.openInputStream(uri)?.use { inputStream ->
                    val reader = BufferedReader(InputStreamReader(inputStream))
                    val pattern = Pattern.compile("removeparam=([a-zA-Z0-9_-]+)", Pattern.CASE_INSENSITIVE)
                    val newParams = mutableSetOf<String>()
                    
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        val matcher = pattern.matcher(line!!)
                        while (matcher.find()) {
                            newParams.add(matcher.group(1).lowercase().trim())
                        }
                    }
                    
                    for (param in newParams) {
                        repository.addCustomTrackingParam(param)
                        addedCount++
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            withContext(Dispatchers.Main) {
                onResult(addedCount)
            }
        }
    }
}

