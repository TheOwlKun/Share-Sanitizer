package org.sharesanitizer.app.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsRepository(private val context: Context) {
    private val PREF_TRIM_WHITESPACE = booleanPreferencesKey("trim_whitespace")
    private val PREF_COLLAPSE_LINES = booleanPreferencesKey("collapse_lines")
    private val PREF_STRIP_INVISIBLE = booleanPreferencesKey("strip_invisible_chars")
    private val PREF_UNWRAP_REDIRECTS = booleanPreferencesKey("unwrap_redirects")
    private val PREF_EXPORT_FORMAT = stringPreferencesKey("export_format")
    private val PREF_IMAGE_QUALITY = intPreferencesKey("image_quality")
    private val PREF_FILENAME_SUFFIX = stringPreferencesKey("filename_suffix")
    private val PREF_AUTO_DELETE_CACHE = booleanPreferencesKey("auto_delete_cache")
    private val PREF_THEME_PREFERENCE = intPreferencesKey("theme_preference")
    private val PREF_CUSTOM_TRACKING_PARAMS = stringPreferencesKey("custom_tracking_params")

    val trimWhitespace: Flow<Boolean> = context.dataStore.data.map { it[PREF_TRIM_WHITESPACE] ?: false }
    val collapseLines: Flow<Boolean> = context.dataStore.data.map { it[PREF_COLLAPSE_LINES] ?: false }
    val stripInvisibleChars: Flow<Boolean> = context.dataStore.data.map { it[PREF_STRIP_INVISIBLE] ?: true }
    val unwrapRedirects: Flow<Boolean> = context.dataStore.data.map { it[PREF_UNWRAP_REDIRECTS] ?: true }
    val exportFormat: Flow<String> = context.dataStore.data.map { it[PREF_EXPORT_FORMAT] ?: "ORIGINAL" }
    val imageQuality: Flow<Int> = context.dataStore.data.map { it[PREF_IMAGE_QUALITY] ?: 90 }
    val filenameSuffix: Flow<String> = context.dataStore.data.map { it[PREF_FILENAME_SUFFIX] ?: "_clean" }
    val autoDeleteCache: Flow<Boolean> = context.dataStore.data.map { it[PREF_AUTO_DELETE_CACHE] ?: true }
    val themePreference: Flow<Int> = context.dataStore.data.map { it[PREF_THEME_PREFERENCE] ?: 0 }
    val customTrackingParams: Flow<Set<String>> = context.dataStore.data.map { prefs ->
        val savedString = prefs[PREF_CUSTOM_TRACKING_PARAMS] ?: ""
        if (savedString.isBlank()) emptySet() else savedString.split(",").toSet()
    }

    suspend fun setTrimWhitespace(value: Boolean) = context.dataStore.edit { it[PREF_TRIM_WHITESPACE] = value }
    suspend fun setCollapseLines(value: Boolean) = context.dataStore.edit { it[PREF_COLLAPSE_LINES] = value }
    suspend fun setStripInvisibleChars(value: Boolean) = context.dataStore.edit { it[PREF_STRIP_INVISIBLE] = value }
    suspend fun setUnwrapRedirects(value: Boolean) = context.dataStore.edit { it[PREF_UNWRAP_REDIRECTS] = value }
    suspend fun setExportFormat(format: String) = context.dataStore.edit { it[PREF_EXPORT_FORMAT] = format }
    suspend fun setImageQuality(quality: Int) = context.dataStore.edit { it[PREF_IMAGE_QUALITY] = quality }
    suspend fun setFilenameSuffix(suffix: String) = context.dataStore.edit { it[PREF_FILENAME_SUFFIX] = suffix }
    suspend fun setAutoDeleteCache(value: Boolean) = context.dataStore.edit { it[PREF_AUTO_DELETE_CACHE] = value }
    suspend fun setThemePreference(theme: Int) = context.dataStore.edit { it[PREF_THEME_PREFERENCE] = theme }

    suspend fun addCustomTrackingParam(param: String) = context.dataStore.edit { prefs ->
        val currentString = prefs[PREF_CUSTOM_TRACKING_PARAMS] ?: ""
        val currentSet = if (currentString.isBlank()) emptySet() else currentString.split(",").toSet()
        val newSet = currentSet + param.lowercase().trim()
        prefs[PREF_CUSTOM_TRACKING_PARAMS] = newSet.joinToString(",")
    }

    suspend fun removeCustomTrackingParam(param: String) = context.dataStore.edit { prefs ->
        val currentString = prefs[PREF_CUSTOM_TRACKING_PARAMS] ?: ""
        val currentSet = if (currentString.isBlank()) emptySet() else currentString.split(",").toSet()
        val newSet = currentSet - param.lowercase().trim()
        prefs[PREF_CUSTOM_TRACKING_PARAMS] = newSet.joinToString(",")
    }
}
