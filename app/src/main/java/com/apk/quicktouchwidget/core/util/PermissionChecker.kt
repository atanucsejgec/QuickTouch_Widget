package com.apk.quicktouchwidget.core.util

import android.accessibilityservice.AccessibilityService
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import androidx.core.content.ContextCompat
import com.apk.quicktouchwidget.admin.MyDeviceAdminReceiver

/**
 * Utility object for checking various permissions and device capabilities.
 * Provides centralized permission and feature checks used across all widgets.
 */
object PermissionChecker {

    private const val TAG = "PermissionChecker"

    /**
     * Checks whether a runtime permission is granted.
     *
     * @param context The application context
     * @param permission The permission string (e.g., [android.Manifest.permission.CAMERA])
     * @return true if the permission is granted, false otherwise
     */
    fun hasPermission(context: Context, permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Checks whether the app's Device Admin receiver is currently active.
     *
     * @param context The application context
     * @return true if the device admin is active, false otherwise
     */
    fun isDeviceAdminActive(context: Context): Boolean {
        return try {
            val dpm = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as? DevicePolicyManager
            val componentName = MyDeviceAdminReceiver.getComponentName(context)
            dpm?.isAdminActive(componentName) == true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to check device admin status: ${e.message}", e)
            false
        }
    }

    /**
     * Checks whether a specific AccessibilityService is enabled in system settings.
     *
     * @param context The application context
     * @param serviceClass The AccessibilityService class to check
     * @return true if the service is enabled, false otherwise
     */
    fun isAccessibilityServiceEnabled(
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
            Log.e(TAG, "Failed to check accessibility service status: ${e.message}", e)
            false
        }
    }

    /**
     * Checks whether the device has a camera flash feature.
     *
     * @param context The application context
     * @return true if the device has a camera flash, false otherwise
     */
    fun hasCameraFlash(context: Context): Boolean {
        return context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)
    }
}
