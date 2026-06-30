package com.apk.quicktouchwidget.widget.flashlight

import androidx.glance.appwidget.GlanceAppWidgetReceiver

/**
 * Broadcast receiver that binds the [FlashlightWidget] to the Android
 * widget framework.
 *
 * Declared in the manifest with the appropriate `<receiver>` entry and
 * `<appwidget-provider>` metadata so the system can instantiate and
 * manage the flashlight widget lifecycle.
 */
class FlashlightReceiver : GlanceAppWidgetReceiver() {

    /** The Glance widget instance managed by this receiver. */
    override val glanceAppWidget = FlashlightWidget()
}
