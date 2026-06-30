package com.apk.quicktouchwidget.widget.shortcut

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.clickable
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.apk.quicktouchwidget.R
import com.apk.quicktouchwidget.core.base.BaseWidget

/**
 * URL Shortcut widget that opens a user-configured URL.
 * Each widget instance can have a different URL configured via [ShortcutConfigActivity].
 */
class ShortcutWidget : BaseWidget() {

    companion object {
        /** Preference key for the stored URL. */
        val URL_KEY = stringPreferencesKey("shortcut_url")
        /** Preference key for the stored label. */
        val LABEL_KEY = stringPreferencesKey("shortcut_label")
    }

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            val prefs = currentState<Preferences>()
            val url = prefs[URL_KEY] ?: ""
            val label = prefs[LABEL_KEY] ?: ""

            ShortcutContent(
                url = url,
                label = label,
                isConfigured = url.isNotBlank()
            )
        }
    }
}

/**
 * Composable content for the Shortcut widget.
 */
@Composable
private fun ShortcutContent(url: String, label: String, isConfigured: Boolean) {
    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(ColorProvider(BaseWidget.BackgroundColor))
            .cornerRadius(16.dp)
            .clickable(onClick = actionRunCallback<ShortcutAction>()),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = GlanceModifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                provider = ImageProvider(
                    if (isConfigured) R.drawable.ic_link else R.drawable.ic_warning
                ),
                contentDescription = "URL Shortcut",
                modifier = GlanceModifier.size(36.dp)
            )
            Spacer(modifier = GlanceModifier.height(4.dp))
            Text(
                text = when {
                    !isConfigured -> "No URL"
                    label.isNotBlank() -> label
                    else -> url.take(15)
                },
                style = TextStyle(
                    color = ColorProvider(
                        if (isConfigured) BaseWidget.TextColor else BaseWidget.WarningColor
                    ),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                ),
                maxLines = 1
            )
            if (!isConfigured) {
                Text(
                    text = "Tap to setup",
                    style = TextStyle(
                        color = ColorProvider(BaseWidget.DisabledColor),
                        fontSize = 10.sp,
                        textAlign = TextAlign.Center
                    )
                )
            }
        }
    }
}
