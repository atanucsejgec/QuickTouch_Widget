package com.apk.quicktouchwidget.ui.screen

import android.Manifest
import android.content.Context
import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.apk.quicktouchwidget.R
import com.apk.quicktouchwidget.core.model.WidgetCategory
import com.apk.quicktouchwidget.core.model.WidgetConfig
import com.apk.quicktouchwidget.core.registry.WidgetRegistry
import com.apk.quicktouchwidget.core.util.PermissionChecker
import com.apk.quicktouchwidget.service.PowerMenuAccessibilityService
import com.apk.quicktouchwidget.ui.components.PermissionBanner
import com.apk.quicktouchwidget.ui.components.WidgetCard
import com.apk.quicktouchwidget.ui.components.WidgetStatus
import com.apk.quicktouchwidget.ui.theme.AppBackground

/**
 * Home screen displaying all available widgets from the registry.
 * Reads ONLY from [WidgetRegistry] — zero widget-specific code.
 *
 * @param onNavigateToPermissions Callback to navigate to permissions screen
 * @param onNavigateToSettings Callback to navigate to settings screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToPermissions: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val context = LocalContext.current
    val widgets = remember { WidgetRegistry.widgets }
    val hasSetupNeeded = remember(widgets) {
        widgets.any { getWidgetStatus(it, context) == WidgetStatus.SETUP_REQUIRED }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding()
    ) {
        TopAppBar(
            title = {
                Text(
                    text = stringResource(R.string.home_title),
                    style = MaterialTheme.typography.headlineMedium
                )
            },
            actions = {
                IconButton(onClick = onNavigateToSettings) {
                    Icon(
                        imageVector = Icons.Filled.Settings,
                        contentDescription = stringResource(R.string.nav_settings),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = AppBackground,
                titleContentColor = MaterialTheme.colorScheme.onSurface
            )
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // Permission banner at top
            if (hasSetupNeeded) {
                item(span = { GridItemSpan(2) }) {
                    PermissionBanner(
                        message = "Some widgets need permissions to work",
                        actionLabel = stringResource(R.string.grant_permission),
                        onActionClick = onNavigateToPermissions
                    )
                }
                item(span = { GridItemSpan(2) }) {
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }

            // Group by category
            WidgetCategory.entries.forEach { category ->
                val categoryWidgets = widgets.filter { it.category == category }
                if (categoryWidgets.isNotEmpty()) {
                    item(span = { GridItemSpan(2) }) {
                        Text(
                            text = when (category) {
                                WidgetCategory.FUNCTIONAL -> stringResource(R.string.category_functional)
                                WidgetCategory.SHORTCUT -> stringResource(R.string.category_shortcut)
                                WidgetCategory.SETTINGS -> stringResource(R.string.category_settings)
                            },
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                        )
                    }

                    items(categoryWidgets, key = { it.widgetId }) { widget ->
                        WidgetCard(
                            config = widget,
                            status = getWidgetStatus(widget, context),
                            onClick = {
                                if (getWidgetStatus(widget, context) == WidgetStatus.SETUP_REQUIRED) {
                                    onNavigateToPermissions()
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Determines the current status of a widget based on device compatibility and permissions.
 */
private fun getWidgetStatus(config: WidgetConfig, context: Context): WidgetStatus {
    // Check Android version
    if (Build.VERSION.SDK_INT < config.minAndroidVersion) {
        return WidgetStatus.NOT_SUPPORTED
    }

    // Check device availability
    if (!config.isAvailable(context)) {
        return WidgetStatus.NOT_SUPPORTED
    }

    // Check permissions
    for (permission in config.requiresPermission) {
        when (permission) {
            Manifest.permission.CAMERA -> {
                if (!PermissionChecker.hasPermission(context, permission)) {
                    return WidgetStatus.SETUP_REQUIRED
                }
            }
            "android.permission.BIND_DEVICE_ADMIN" -> {
                if (!PermissionChecker.isDeviceAdminActive(context)) {
                    return WidgetStatus.SETUP_REQUIRED
                }
            }
            "android.permission.BIND_ACCESSIBILITY_SERVICE" -> {
                if (!PermissionChecker.isAccessibilityServiceEnabled(
                        context, PowerMenuAccessibilityService::class.java
                    )) {
                    return WidgetStatus.SETUP_REQUIRED
                }
            }
        }
    }

    return WidgetStatus.READY
}
