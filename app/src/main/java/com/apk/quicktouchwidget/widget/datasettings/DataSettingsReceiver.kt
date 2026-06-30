package com.apk.quicktouchwidget.widget.datasettings

import androidx.glance.appwidget.GlanceAppWidgetReceiver

/**
 * Broadcast receiver for the Data Settings widget.
 */
class DataSettingsReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget = DataSettingsWidget()
}
