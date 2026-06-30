package com.apk.quicktouchwidget.widget.usbtethering

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import com.apk.quicktouchwidget.core.base.BaseAction

class UsbTetheringAction : BaseAction() {

    override suspend fun onExecute(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        // Strategy 1: Direct Intent to Tether Settings (Commonly used)
        try {
            val intent = Intent("android.settings.TETHER_SETTINGS").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                // Highlight the specific USB tethering option if supported
                putExtra(":settings:fragment_args_key", "usb_tether_settings")
                putExtra("EXTRA_FRAGMENT_ARG_KEY", "usb_tether_settings")
            }
            context.startActivity(intent)
            return
        } catch (e: Exception) {
            // Fall through
        }

        // Strategy 2: App Search Settings (Modern Android search integration)
        try {
            val searchIntent = Intent("android.settings.APP_SEARCH_SETTINGS").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                putExtra("query", "USB tethering")
            }
            context.startActivity(searchIntent)
            return
        } catch (e: ActivityNotFoundException) {
            // Fall through
        }

        // Strategy 3: Generic settings with search query and fragment key
        try {
            val searchIntent = Intent(Settings.ACTION_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                putExtra("query", "USB tethering")
                putExtra(":settings:fragment_args_key", "usb_tether_settings")
                putExtra("EXTRA_FRAGMENT_ARG_KEY", "usb_tether_settings")
            }
            context.startActivity(searchIntent)
        } catch (e: Exception) {
            // Final fallback to generic Settings
            try {
                val settingsIntent = Intent(Settings.ACTION_SETTINGS).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(settingsIntent)
            } catch (finalException: Exception) {
                // Ignore if everything fails
            }
        }
    }
}
