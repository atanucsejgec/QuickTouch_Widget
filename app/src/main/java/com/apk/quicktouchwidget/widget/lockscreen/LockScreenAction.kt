package com.apk.quicktouchwidget.widget.lockscreen

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import com.apk.quicktouchwidget.core.base.BaseAction

/**
 * Action callback triggered when the user taps the lock-screen widget.
 *
 * Behaviour depends on the current device-admin state:
 * - **Admin active**: immediately locks the screen via [DeviceAdminHelper.lockScreen].
 * - **Admin inactive**: launches the system device-admin activation prompt
 *   so the user can grant the required permission.
 */
class LockScreenAction : BaseAction() {

    override suspend fun onExecute(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        if (DeviceAdminHelper.isAdminActive(context)) {
            // Lock the screen immediately.
            DeviceAdminHelper.lockScreen(context)
        } else {
            // Open the device-admin activation prompt.
            try {
                val intent = DeviceAdminHelper.getActivationIntent(context)
                context.startActivity(intent)
            } catch (e: Exception) {
                // Rare: activity could not be started (e.g. restricted profile).
            }
        }

        // Refresh the widget UI so the state label updates if admin was just activated.
        LockScreenWidget().update(context, glanceId)
    }
}
