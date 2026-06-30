package com.apk.quicktouchwidget.widget.devoptions

import androidx.glance.appwidget.GlanceAppWidgetReceiver

/**
 * Broadcast receiver for the Developer Options widget.
 */
class DevOptionsReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget = DevOptionsWidget()
}
