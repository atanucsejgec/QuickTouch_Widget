package com.apk.quicktouchwidget.widget.datasettings

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.util.Log
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import com.apk.quicktouchwidget.core.base.BaseAction

/**
 * Action callback for the Data Settings widget.
 * Opens mobile data settings page.
 */
class DataSettingsAction : BaseAction() {

    companion object {
        private const val TAG = "DataSettingsAction"
    }

    override suspend fun onExecute(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        try {
            val intent = Intent(Settings.ACTION_DATA_ROAMING_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.w(TAG, "ACTION_DATA_ROAMING_SETTINGS not available, trying fallback", e)
            try {
                val fallback = Intent(Settings.ACTION_WIRELESS_SETTINGS).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(fallback)
            } catch (e2: Exception) {
                Log.e(TAG, "Could not open any network settings: ${e2.message}", e2)
            }
        }
    }
}
