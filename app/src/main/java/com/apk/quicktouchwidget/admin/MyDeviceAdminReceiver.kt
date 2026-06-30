package com.apk.quicktouchwidget.admin

import android.app.admin.DeviceAdminReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log

/**
 * Device Admin Receiver for the QuickTouch Widget app.
 *
 * This receiver is required for the Lock Screen widget to use
 * [android.app.admin.DevicePolicyManager.lockNow] to lock the device.
 *
 * The user must explicitly activate this device admin through
 * the system's Device Admin settings or via an activation intent.
 */
class MyDeviceAdminReceiver : DeviceAdminReceiver() {

    companion object {
        private const val TAG = "MyDeviceAdminReceiver"

        /**
         * Returns the [ComponentName] for this receiver.
         * Used by [android.app.admin.DevicePolicyManager] to identify
         * this admin component.
         *
         * @param context The application context
         * @return The [ComponentName] for [MyDeviceAdminReceiver]
         */
        fun getComponentName(context: Context): ComponentName {
            return ComponentName(context, MyDeviceAdminReceiver::class.java)
        }
    }

    /**
     * Called when the user has enabled this device admin.
     *
     * @param context The running context
     * @param intent The received intent
     */
    override fun onEnabled(context: Context, intent: Intent) {
        super.onEnabled(context, intent)
        Log.d(TAG, "Device admin enabled")
    }

    /**
     * Called when the user has disabled this device admin.
     *
     * @param context The running context
     * @param intent The received intent
     */
    override fun onDisabled(context: Context, intent: Intent) {
        super.onDisabled(context, intent)
        Log.d(TAG, "Device admin disabled")
    }
}
