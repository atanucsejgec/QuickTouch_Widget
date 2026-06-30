package com.apk.quicktouchwidget

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.apk.quicktouchwidget.ui.screen.HomeScreen
import com.apk.quicktouchwidget.ui.screen.PermissionScreen
import com.apk.quicktouchwidget.ui.screen.SettingsScreen
import com.apk.quicktouchwidget.ui.theme.AppBackground
import com.apk.quicktouchwidget.ui.theme.QuickTouchWidgetTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Navigation route constants.
 */
private object Routes {
    const val HOME = "home"
    const val PERMISSIONS = "permissions"
    const val SETTINGS = "settings"
}

/**
 * Main entry point for the QuickTouch Widget app.
 *
 * This activity reads ONLY from [com.apk.quicktouchwidget.core.registry.WidgetRegistry].
 * It contains ZERO widget-specific code. All widget information comes from the registry.
 *
 * Navigation:
 * - Home: Grid of all registered widgets with status
 * - Permissions: Step-by-step permission guide
 * - Settings: App preferences and about
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            QuickTouchWidgetTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = AppBackground
                ) {
                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = Routes.HOME
                    ) {
                        composable(Routes.HOME) {
                            HomeScreen(
                                onNavigateToPermissions = {
                                    navController.navigate(Routes.PERMISSIONS)
                                },
                                onNavigateToSettings = {
                                    navController.navigate(Routes.SETTINGS)
                                }
                            )
                        }

                        composable(Routes.PERMISSIONS) {
                            PermissionScreen(
                                onBack = { navController.popBackStack() }
                            )
                        }

                        composable(Routes.SETTINGS) {
                            SettingsScreen(
                                onBack = { navController.popBackStack() },
                                onNavigateToPermissions = {
                                    navController.navigate(Routes.PERMISSIONS)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}