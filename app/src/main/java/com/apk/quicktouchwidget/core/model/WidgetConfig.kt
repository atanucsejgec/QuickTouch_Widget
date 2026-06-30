package com.apk.quicktouchwidget.core.model

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.glance.appwidget.GlanceAppWidgetReceiver

/**
 * Enum representing widget categories for grouping in the UI.
 */
enum class WidgetCategory {
    /** Widgets that perform a device function (flashlight, volume, lock, power) */
    FUNCTIONAL,
    /** Widgets that open URLs or apps */
    SHORTCUT,
    /** Widgets that open system settings pages */
    SETTINGS
}

/**
 * Configuration data class for a single widget.
 * Used by [com.apk.quicktouchwidget.core.registry.WidgetRegistry] to describe each available widget.
 *
 * @property widgetId Unique string identifier for this widget
 * @property widgetName String resource ID for the display name
 * @property widgetDescription String resource ID for the short description
 * @property iconRes Drawable resource ID for the widget icon
 * @property receiverClass The GlanceAppWidgetReceiver class for this widget
 * @property requiresPermission List of permission strings needed
 * @property minAndroidVersion Minimum API level required
 * @property category The widget category for UI grouping
 * @property isAvailable Lambda to check runtime device compatibility
 */
data class WidgetConfig(
    val widgetId: String,
    @StringRes val widgetName: Int,
    @StringRes val widgetDescription: Int,
    @DrawableRes val iconRes: Int,
    val receiverClass: Class<out GlanceAppWidgetReceiver>,
    val requiresPermission: List<String> = emptyList(),
    val minAndroidVersion: Int = android.os.Build.VERSION_CODES.N,
    val category: WidgetCategory = WidgetCategory.FUNCTIONAL,
    val isAvailable: (Context) -> Boolean = { true }
)
