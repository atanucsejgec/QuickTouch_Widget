package com.apk.quicktouchwidget.core.base

import androidx.compose.ui.graphics.Color
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.state.PreferencesGlanceStateDefinition

/**
 * Represents the current state of a widget.
 */
enum class WidgetState {
    /** Widget is ready and fully functional */
    READY,
    /** Widget requires initial setup or configuration */
    SETUP_REQUIRED,
    /** Widget is not supported on this device */
    NOT_SUPPORTED,
    /** Widget is performing an operation */
    LOADING,
    /** Widget encountered an error */
    ERROR
}

/**
 * Abstract base class for all QuickTouch widgets.
 * Provides common theme colors and state management via [PreferencesGlanceStateDefinition].
 *
 * All widget implementations should extend this class to inherit
 * consistent theming and state handling.
 */
abstract class BaseWidget : GlanceAppWidget() {

    override val stateDefinition = PreferencesGlanceStateDefinition

    companion object {
        /** Dark background color #1A1A2E */
        val BackgroundColor = Color(0xFF1A1A2E)
        /** Accent color #E94560 */
        val AccentColor = Color(0xFFE94560)
        /** White text color */
        val TextColor = Color(0xFFFFFFFF)
        /** Surface color #16213E */
        val SurfaceColor = Color(0xFF16213E)
        /** Secondary color #0F3460 */
        val SecondaryColor = Color(0xFF0F3460)
        /** Success/On color */
        val SuccessColor = Color(0xFF4CAF50)
        /** Off/Disabled color */
        val DisabledColor = Color(0xFF757575)
        /** Warning color */
        val WarningColor = Color(0xFFFFC107)
    }
}
