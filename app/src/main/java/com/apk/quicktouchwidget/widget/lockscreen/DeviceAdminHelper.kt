package com.apk.quicktouchwidget.widget.lockscreen

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import com.apk.quicktouchwidget.admin.MyDeviceAdminReceiver

/**
 * Helper for device-admin operations required by the lock-screen widget.
 *
 * Wraps [DevicePolicyManager] interactions and provides convenience
 * methods for checking admin status, locking the screen, and building
 * the activation intent.
 */
object DeviceAdminHelper {

    /**
     * Returns the [DevicePolicyManager] system service.
     *
     * @param context Any valid context.
     */
    private fun devicePolicyManager(context: Context): DevicePolicyManager =
        context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager

    /**
     * Checks whether our [MyDeviceAdminReceiver] is currently an active
     * device administrator.
     *
     * @param context Any valid context.
     * @return `true` if device admin is active.
     */
    fun isAdminActive(context: Context): Boolean {
        return try {
            val componentName = MyDeviceAdminReceiver.getComponentName(context)
            devicePolicyManager(context).isAdminActive(componentName)
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Immediately locks the device screen.
     *
     * Requires that our admin receiver is already an active device
     * administrator. If the admin is not active or a security exception
     * occurs, the call fails silently.
     *
     * @param context Any valid context.
     * @return `true` if the lock was initiated successfully.
     */
    fun lockScreen(context: Context): Boolean {
        return try {
            if (!isAdminActive(context)) return false
            devicePolicyManager(context).lockNow()
            true
        } catch (e: SecurityException) {
            // Admin privilege was revoked between the check and the call.
            false
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Builds an [Intent] that launches the system device-admin activation
     * prompt for [MyDeviceAdminReceiver].
     *
     * The returned intent should be started with [Intent.FLAG_ACTIVITY_NEW_TASK]
     * when launched from a widget context.
     *
     * @param context Any valid context.
     * @return An intent configured for [DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN].
     */
    fun getActivationIntent(context: Context): Intent {
        val componentName = MyDeviceAdminReceiver.getComponentName(context)
        return Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN).apply {
            putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName)
            putExtra(
                DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                "QuickTouch Widget needs device admin permission to lock your screen."
            )
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }
}
