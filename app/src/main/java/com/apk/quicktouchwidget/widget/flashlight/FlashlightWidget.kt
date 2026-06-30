package com.apk.quicktouchwidget.widget.flashlight

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
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
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.apk.quicktouchwidget.R
import com.apk.quicktouchwidget.core.base.BaseWidget

/**
 * Glance widget that provides a one-tap flashlight toggle.
 *
 * Displays the current torch state (ON / OFF) with an appropriate icon.
 * Tapping anywhere on the widget toggles the flashlight via [FlashlightAction].
 *
 * Recommended size: **2 × 1** home-screen cells.
 */
class FlashlightWidget : BaseWidget() {

    companion object {
        /** Preferences key storing the current torch state. */
        val FLASHLIGHT_ON_KEY = booleanPreferencesKey("flashlight_on")
    }

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        // Ensure torch callback is registered so cached state stays fresh.
        FlashlightManager.registerTorchCallback(context)

        provideContent {
            val prefs = currentState<Preferences>()
            val isOn = prefs[FLASHLIGHT_ON_KEY] ?: false
            FlashlightWidgetContent(isOn = isOn)
        }
    }
}

/**
 * Composable content for the flashlight widget.
 *
 * @param isOn Whether the flashlight is currently on.
 */
@Composable
fun FlashlightWidgetContent(isOn: Boolean) {
    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(ColorProvider(BaseWidget.BackgroundColor))
            .cornerRadius(16.dp)
            .clickable(onClick = actionRunCallback<FlashlightAction>()),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = GlanceModifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Flashlight icon – changes based on state.
            Box(
                modifier = GlanceModifier
                    .size(48.dp)
                    .background(
                        ColorProvider(
                            if (isOn) BaseWidget.SuccessColor else BaseWidget.SurfaceColor
                        )
                    )
                    .cornerRadius(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    provider = ImageProvider(
                        if (isOn) R.drawable.ic_flashlight else R.drawable.ic_flashlight_off
                    ),
                    contentDescription = if (isOn) "Flashlight on" else "Flashlight off",
                    modifier = GlanceModifier.size(36.dp)
                )
            }

            Spacer(modifier = GlanceModifier.width(12.dp))

            // State text.
            Column {
                Text(
                    text = "Flashlight",
                    style = TextStyle(
                        color = ColorProvider(BaseWidget.TextColor),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = GlanceModifier.height(2.dp))
                Text(
                    text = if (isOn) "ON" else "OFF",
                    style = TextStyle(
                        color = ColorProvider(
                            if (isOn) BaseWidget.SuccessColor else BaseWidget.DisabledColor
                        ),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        }
    }
}
