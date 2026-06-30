package com.apk.quicktouchwidget.widget.volume

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.state.updateAppWidgetState
import com.apk.quicktouchwidget.core.base.BaseAction

/**
 * Action callback that toggles media stream mute.
 *
 * After toggling, the new mute state and volume level are persisted
 * into the widget's preferences and the UI is refreshed.
 */
class VolumeMuteAction : BaseAction() {

    override suspend fun onExecute(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        VolumeManager.toggleMute(context)

        val newLevel = VolumeManager.getCurrentVolume(context)
        val isMuted = VolumeManager.isMuted(context)

        updateAppWidgetState(context, glanceId) { prefs ->
            prefs[VolumeWidget.VOLUME_LEVEL_KEY] = newLevel
            prefs[VolumeWidget.VOLUME_MUTED_KEY] = isMuted
        }

        VolumeWidget().update(context, glanceId)
    }
}
