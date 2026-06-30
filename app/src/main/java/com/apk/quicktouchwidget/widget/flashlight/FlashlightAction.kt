package com.apk.quicktouchwidget.widget.flashlight

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.state.updateAppWidgetState
import com.apk.quicktouchwidget.core.base.BaseAction

/**
 * Action callback triggered when the user taps the flashlight widget.
 *
 * Toggles the device torch via [FlashlightManager] and persists the
 * resulting state into the widget's [Preferences] so the UI updates
 * immediately on the next recomposition.
 */
class FlashlightAction : BaseAction() {

    /**
     * Executes the flashlight toggle.
     *
     * @param context Widget host context.
     * @param glanceId Unique identifier of the widget instance.
     * @param parameters Action parameters (unused for this action).
     */
    override suspend fun onExecute(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        // Toggle the torch and capture the new state.
        val isOn = FlashlightManager.toggleFlashlight(context)

        // Persist the new state so the widget UI reflects it.
        updateAppWidgetState(context, glanceId) { prefs ->
            prefs[FlashlightWidget.FLASHLIGHT_ON_KEY] = isOn
        }

        // Trigger a widget UI refresh.
        FlashlightWidget().update(context, glanceId)
    }
}
