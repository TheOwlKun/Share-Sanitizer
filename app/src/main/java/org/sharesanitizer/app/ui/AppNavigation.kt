package org.sharesanitizer.app.ui

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.sharesanitizer.app.viewmodel.SettingsViewModel
import org.sharesanitizer.app.viewmodel.ShareViewModel
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun AppNavigation(
    initialRoute: String = "home",
    navController: NavHostController = rememberNavController()
) {
    val shareViewModel: ShareViewModel = viewModel()
    val settingsViewModel: SettingsViewModel = viewModel()

    NavHost(
        navController = navController, 
        startDestination = initialRoute,
        enterTransition = {
            fadeIn(animationSpec = tween(300)) + scaleIn(initialScale = 0.9f, animationSpec = tween(300))
        },
        exitTransition = {
            fadeOut(animationSpec = tween(300)) + scaleOut(targetScale = 1.1f, animationSpec = tween(300))
        },
        popEnterTransition = {
            fadeIn(animationSpec = tween(300)) + scaleIn(initialScale = 1.1f, animationSpec = tween(300))
        },
        popExitTransition = {
            fadeOut(animationSpec = tween(300)) + scaleOut(targetScale = 0.9f, animationSpec = tween(300))
        }
    ) {
        composable("home") {
            HomeScreen(
                onNavigateToText = { navController.navigate("text_sanitize") },
                onNavigateToImage = { navController.navigate("image_sanitize") },
                onNavigateToSettings = { navController.navigate("settings") },
                onNavigateToAbout = { navController.navigate("about") },
                shareViewModel = shareViewModel
            )
        }
        composable("text_sanitize") {
            TextSanitizeScreen(
                viewModel = shareViewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable("image_sanitize") {
            ImageSanitizeScreen(
                viewModel = shareViewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable("settings") {
            SettingsScreen(
                viewModel = settingsViewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable("about") {
            AboutScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}

