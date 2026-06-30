package com.apk.quicktouchwidget.widget.powermenu

import android.content.Context
import android.util.Log
import com.apk.quicktouchwidget.core.util.PermissionChecker
import com.apk.quicktouchwidget.service.PowerMenuAccessibilityService

/**
 * Helper class for power menu functionality.
 * Bridges the widget action with the accessibility service.
 */
object PowerMenuService {

    private const val TAG = "PowerMenuService"

    /**
     * Checks if the accessibility service is enabled.
     *
     * @param context Application context
     * @return true if the service is enabled
     */
    fun isServiceEnabled(context: Context): Boolean {
        return PermissionChecker.isAccessibilityServiceEnabled(
            context,
            PowerMenuAccessibilityService::class.java
        )
    }

    /**
     * Attempts to show the power dialog via the accessibility service.
     *
     * @return true if the dialog was triggered successfully
     */
    fun showPowerDialog(): Boolean {
        return try {
            val service = PowerMenuAccessibilityService.instance
            if (service != null) {
                service.showPowerDialog()
                true
            } else {
                Log.w(TAG, "Accessibility service instance not available")
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to show power dialog: ${e.message}", e)
            false
        }
    }
}
