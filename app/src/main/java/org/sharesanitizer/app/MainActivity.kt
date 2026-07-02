package org.sharesanitizer.app

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.Modifier
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import org.sharesanitizer.app.settings.SettingsRepository
import org.sharesanitizer.app.ui.AppNavigation
import org.sharesanitizer.app.ui.theme.ShareSanitizerTheme
import org.sharesanitizer.app.viewmodel.ShareViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Clean up stale cache from previous sessions (more reliable than onDestroy)
        cleanCacheIfEnabled()

        try {
            org.woheller69.freeDroidWarn.FreeDroidWarn.showWarningOnUpgrade(this, R.mipmap.ic_launcher)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        enableEdgeToEdge()
        setContent {
            val settingsViewModel: org.sharesanitizer.app.viewmodel.SettingsViewModel = viewModel()
            val themePreference by settingsViewModel.themePreference.collectAsState()
            
            val darkTheme = when(themePreference) {
                1 -> false
                2 -> true
                else -> isSystemInDarkTheme()
            }

            ShareSanitizerTheme(darkTheme = darkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Transparent
                ) {
                    val colorScheme = MaterialTheme.colorScheme
                    Box(modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(colorScheme.surfaceVariant, colorScheme.background),
                                center = Offset(0f, 0f),
                                radius = 2500f
                            )
                        )
                    ) {
                        val navController = rememberNavController()
                        val shareViewModel: ShareViewModel = viewModel()

                        LaunchedEffect(Unit) {
                            shareViewModel.notifications.collect { message ->
                                android.widget.Toast.makeText(this@MainActivity, message, android.widget.Toast.LENGTH_SHORT).show()
                            }
                        }

                        LaunchedEffect(intent) {
                            handleIntent(intent, shareViewModel, navController)
                        }

                        AppNavigation(navController = navController)
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
    }

    private fun handleIntent(
        intent: Intent?,
        viewModel: ShareViewModel,
        navController: androidx.navigation.NavController
    ) {
        if (intent == null) return

        when (intent.action) {
            Intent.ACTION_SEND -> {
                if (intent.type == "text/plain") {
                    val sharedText = intent.getStringExtra(Intent.EXTRA_TEXT)
                    if (sharedText != null) {
                        viewModel.sanitizeText(sharedText)
                        navController.navigate("text_sanitize")
                    }
                } else if (intent.type?.startsWith("image/") == true) {
                    val imageUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        intent.getParcelableExtra(Intent.EXTRA_STREAM, Uri::class.java)
                    } else {
                        @Suppress("DEPRECATION")
                        intent.getParcelableExtra(Intent.EXTRA_STREAM)
                    }
                    if (imageUri != null) {
                        viewModel.sanitizeImages(listOf(imageUri))
                        navController.navigate("image_sanitize")
                    }
                }
            }
            Intent.ACTION_SEND_MULTIPLE -> {
                if (intent.type?.startsWith("image/") == true) {
                    val imageUris = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM, Uri::class.java)
                    } else {
                        @Suppress("DEPRECATION")
                        intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM)
                    }
                    if (imageUris != null && imageUris.isNotEmpty()) {
                        viewModel.sanitizeImages(imageUris)
                        navController.navigate("image_sanitize")
                    }
                }
            }
        }
    }

    private fun cleanCacheIfEnabled() {
        try {
            val settingsRepository = SettingsRepository(this)
            val autoDelete = runBlocking { settingsRepository.autoDeleteCache.first() }
            if (autoDelete) {
                val cacheDir = java.io.File(cacheDir, "shared_images")
                if (cacheDir.exists()) {
                    cacheDir.deleteRecursively()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
