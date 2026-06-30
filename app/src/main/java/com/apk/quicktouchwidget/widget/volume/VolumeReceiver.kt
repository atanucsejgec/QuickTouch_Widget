package com.apk.quicktouchwidget.widget.volume

import androidx.glance.appwidget.GlanceAppWidgetReceiver

/**
 * Broadcast receiver that binds the [VolumeWidget] to the Android
 * widget framework.
 *
 * Declared in the manifest with the appropriate `<receiver>` entry and
 * `<appwidget-provider>` metadata so the system can instantiate and
 * manage the volume widget lifecycle.
 */
class VolumeReceiver : GlanceAppWidgetReceiver() {

    /** The Glance widget instance managed by this receiver. */
    override val glanceAppWidget = VolumeWidget()
}
