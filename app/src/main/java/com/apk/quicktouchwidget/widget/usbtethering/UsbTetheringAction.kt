package com.apk.quicktouchwidget.widget.usbtethering

import android.content.Context
import android.content.Intent
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import com.apk.quicktouchwidget.core.base.BaseAction

class UsbTetheringAction : BaseAction() {

    override suspend fun onExecute(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        // Open the USB tethering / Hotspot settings page
        // Using string action as Settings.ACTION_TETHER_SETTINGS may be hidden on some APIs
        val intent = Intent("android.settings.TETHER_SETTINGS").apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }
}
