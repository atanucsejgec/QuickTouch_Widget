package com.apk.quicktouchwidget.widget.powermenu

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.util.Log
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import com.apk.quicktouchwidget.core.base.BaseAction

/**
 * Action callback for the Power Menu widget.
 * Shows power dialog if service enabled, otherwise opens accessibility settings.
 */
class PowerMenuAction : BaseAction() {

    companion object {
        private const val TAG = "PowerMenuAction"
    }

    override suspend fun onExecute(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        if (PowerMenuService.isServiceEnabled(context)) {
            val success = PowerMenuService.showPowerDialog()
            if (!success) {
                Log.w(TAG, "Power dialog could not be shown, service may not be running")
            }
        } else {
            try {
                val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
            } catch (e: Exception) {
                Log.e(TAG, "Could not open accessibility settings: ${e.message}", e)
            }
        }
    }
}
