package com.apk.quicktouchwidget.widget.devoptions

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.util.Log
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import com.apk.quicktouchwidget.core.base.BaseAction

/**
 * Action callback for the Developer Options widget.
 * Opens developer options settings page.
 */
class DevOptionsAction : BaseAction() {

    companion object {
        private const val TAG = "DevOptionsAction"
    }

    override suspend fun onExecute(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        try {
            val intent = Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.e(TAG, "Could not open developer options. They may not be enabled: ${e.message}", e)
            // Try generic device info settings as fallback
            try {
                val fallback = Intent(Settings.ACTION_DEVICE_INFO_SETTINGS).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(fallback)
            } catch (e2: Exception) {
                Log.e(TAG, "Could not open any settings fallback: ${e2.message}", e2)
            }
        }
    }
}
