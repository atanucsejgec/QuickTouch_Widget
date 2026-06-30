package com.apk.quicktouchwidget.core.base

import android.content.Context
import android.util.Log
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback

/**
 * Abstract base class for all widget action callbacks.
 * Wraps action execution in try-catch with error logging to prevent
 * uncaught exceptions from crashing the widget host process.
 *
 * Subclasses should override [onExecute] to implement action logic.
 */
abstract class BaseAction : ActionCallback {

    companion object {
        private const val TAG = "QuickTouchAction"
    }

    /**
     * Override this to implement the action logic.
     * Called by [onAction] within a try-catch block.
     *
     * @param context The application context
     * @param glanceId The Glance widget instance ID
     * @param parameters Action parameters passed from the widget
     */
    abstract suspend fun onExecute(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    )

    /**
     * Entry point called by the Glance framework.
     * Delegates to [onExecute] with error handling.
     *
     * @param context The application context
     * @param glanceId The Glance widget instance ID
     * @param parameters Action parameters passed from the widget
     */
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        try {
            onExecute(context, glanceId, parameters)
        } catch (e: Exception) {
            Log.e(TAG, "${this::class.simpleName} action failed: ${e.message}", e)
        }
    }
}
