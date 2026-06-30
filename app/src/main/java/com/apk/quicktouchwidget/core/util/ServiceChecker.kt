package com.apk.quicktouchwidget.core.util

import android.accessibilityservice.AccessibilityService
import android.content.ComponentName
import android.content.Context
import android.provider.Settings
import android.text.TextUtils
import android.util.Log

/**
 * Utility object for checking the status of system services
 * used by QuickTouch widgets.
 */
object ServiceChecker {

    private const val TAG = "ServiceChecker"

    /**
     * Checks whether a specific AccessibilityService is currently running
     * by inspecting the system's enabled accessibility services setting.
     *
     * @param context The application context
     * @param serviceClass The AccessibilityService class to check
     * @return true if the service is currently enabled and running, false otherwise
     */
    fun isAccessibilityServiceRunning(
        context: Context,
        serviceClass: Class<out AccessibilityService>
    ): Boolean {
        return try {
            val expectedComponentName = ComponentName(context, serviceClass).flattenToString()
            val enabledServices = Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            )
            if (TextUtils.isEmpty(enabledServices)) {
                return false
            }
            val colonSplitter = TextUtils.SimpleStringSplitter(':')
            colonSplitter.setString(enabledServices)
            while (colonSplitter.hasNext()) {
                val componentNameString = colonSplitter.next()
                if (componentNameString.equals(expectedComponentName, ignoreCase = true)) {
                    return true
                }
            }
            false
        } catch (e: Exception) {
            Log.e(TAG, "Failed to check accessibility service running state: ${e.message}", e)
            false
        }
    }

    /**
     * Returns the [ComponentName] for the app's accessibility service.
     * This is used for enabling/disabling the service via system settings intents.
     *
     * @param context The application context
     * @return The [ComponentName] identifying the app's
     *         [com.apk.quicktouchwidget.service.PowerMenuAccessibilityService]
     */
    fun getAccessibilityServiceComponentName(context: Context): ComponentName {
        return ComponentName(
            context.packageName,
            "${context.packageName}.service.PowerMenuAccessibilityService"
        )
    }
}
