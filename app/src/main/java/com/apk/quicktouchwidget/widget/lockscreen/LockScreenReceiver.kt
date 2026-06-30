package com.apk.quicktouchwidget.widget.lockscreen

import androidx.glance.appwidget.GlanceAppWidgetReceiver

/**
 * Broadcast receiver for the Lock Screen widget.
 * Registered in AndroidManifest.xml to handle widget updates.
 */
class LockScreenReceiver : GlanceAppWidgetReceiver() {

    /** The Glance widget instance this receiver manages. */
    override val glanceAppWidget = LockScreenWidget()
}
