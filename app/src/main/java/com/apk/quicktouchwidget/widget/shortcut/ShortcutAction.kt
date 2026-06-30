package com.apk.quicktouchwidget.widget.shortcut

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.currentState
import com.apk.quicktouchwidget.core.base.BaseAction

/**
 * Action callback for the Shortcut widget.
 * Opens the configured URL or does nothing if not configured.
 */
class ShortcutAction : BaseAction() {

    companion object {
        private const val TAG = "ShortcutAction"
    }

    override suspend fun onExecute(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        // Read URL from widget state via a snapshot
        var url = ""
        updateAppWidgetState(context, glanceId) { prefs ->
            url = prefs[stringPreferencesKey("shortcut_url")] ?: ""
        }

        if (url.isNotBlank()) {
            val success = ShortcutManager.openUrl(context, url)
            if (!success) {
                Log.w(TAG, "Failed to open URL: $url")
            }
        } else {
            Log.d(TAG, "No URL configured for this widget")
        }
    }
}
