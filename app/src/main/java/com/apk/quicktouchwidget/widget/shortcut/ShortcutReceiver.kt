package com.apk.quicktouchwidget.widget.shortcut

import androidx.glance.appwidget.GlanceAppWidgetReceiver

/**
 * Broadcast receiver for the URL Shortcut widget.
 */
class ShortcutReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget = ShortcutWidget()
}
