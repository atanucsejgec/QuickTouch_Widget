package com.apk.quicktouchwidget.widget.powermenu

import androidx.glance.appwidget.GlanceAppWidgetReceiver

/**
 * Broadcast receiver for the Power Menu widget.
 */
class PowerMenuReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget = PowerMenuWidget()
}
