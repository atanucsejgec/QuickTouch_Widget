package com.apk.quicktouchwidget.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.os.Build
import android.util.Log
import android.view.accessibility.AccessibilityEvent

/**
 * AccessibilityService that enables the Power Menu widget to invoke
 * the system power dialog via [GLOBAL_ACTION_POWER_DIALOG].
 *
 * This service requires the user to explicitly enable it in
 * Settings → Accessibility. It maintains a static [instance] reference
 * so that widget actions can call [showPowerDialog] directly.
 *
 * Requires Android Lollipop (API 21) or higher.
 */
class PowerMenuAccessibilityService : AccessibilityService() {

    companion object {
        private const val TAG = "PowerMenuA11yService"

        /**
         * Static reference to the currently running service instance.
         * This is set in [onServiceConnected] and cleared in [onDestroy].
         * Widget actions check this to determine if the service is available.
         */
        var instance: PowerMenuAccessibilityService? = null
            private set
    }

    /**
     * Called when the service is connected by the system.
     * Stores the instance reference and configures the service info.
     */
    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
        Log.d(TAG, "PowerMenuAccessibilityService connected")

        val info = AccessibilityServiceInfo().apply {
            eventTypes = 0
            feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
            notificationTimeout = 0
        }
        serviceInfo = info
    }

    /**
     * Invokes the system power dialog using [GLOBAL_ACTION_POWER_DIALOG].
     * Only available on Android Lollipop (API 21) and above.
     *
     * @return true if the action was performed successfully, false otherwise
     */
    fun showPowerDialog(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val result = performGlobalAction(GLOBAL_ACTION_POWER_DIALOG)
            Log.d(TAG, "showPowerDialog result: $result")
            result
        } else {
            Log.w(TAG, "Power dialog requires API 21+, current: ${Build.VERSION.SDK_INT}")
            false
        }
    }

    /**
     * Called when the system dispatches an AccessibilityEvent.
     * This service does not process any accessibility events.
     *
     * @param event The dispatched accessibility event (unused)
     */
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // No-op: this service only uses global actions
    }

    /**
     * Called when the system wants to interrupt the feedback from this service.
     * This service does not provide continuous feedback, so this is a no-op.
     */
    override fun onInterrupt() {
        // No-op: this service does not provide continuous feedback
    }

    /**
     * Called when the service is being destroyed.
     * Clears the static [instance] reference to prevent memory leaks.
     */
    override fun onDestroy() {
        super.onDestroy()
        instance = null
        Log.d(TAG, "PowerMenuAccessibilityService destroyed")
    }
}
