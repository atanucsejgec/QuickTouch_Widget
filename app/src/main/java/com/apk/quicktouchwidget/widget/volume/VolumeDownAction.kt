package com.apk.quicktouchwidget.widget.volume

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.state.updateAppWidgetState
import com.apk.quicktouchwidget.core.base.BaseAction

/**
 * Action callback that lowers the media volume by one step.
 *
 * After adjusting the volume, the new level and mute state are
 * persisted into the widget's preferences and the UI is refreshed.
 */
class VolumeDownAction : BaseAction() {

    override suspend fun onExecute(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        VolumeManager.volumeDown(context)

        val newLevel = VolumeManager.getCurrentVolume(context)
        val isMuted = VolumeManager.isMuted(context)

        updateAppWidgetState(context, glanceId) { prefs ->
            prefs[VolumeWidget.VOLUME_LEVEL_KEY] = newLevel
            prefs[VolumeWidget.VOLUME_MUTED_KEY] = isMuted
        }

        VolumeWidget().update(context, glanceId)
    }
}
