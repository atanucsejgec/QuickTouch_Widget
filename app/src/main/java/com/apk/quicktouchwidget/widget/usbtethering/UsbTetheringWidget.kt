package com.apk.quicktouchwidget.widget.usbtethering

import android.content.Context
import androidx.compose.runtime.Composable
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

class UsbTetheringWidget : BaseWidget() {

    companion object {
        /** Preference key storing the last-known USB tethering state. */
        val USB_TETHERING_ON_KEY = booleanPreferencesKey("usb_tethering_on")
    }

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        // Sync actual hardware state into prefs before first render so the
        // widget never shows a stale value on placement.
        UsbTetheringManager.syncState(context, id)

        provideContent {
            val prefs = currentState<Preferences>()
            val isOn = prefs[USB_TETHERING_ON_KEY] ?: false
            UsbTetheringWidgetContent(isOn = isOn)
        }
    }
}

// ---------------------------------------------------------------------------
// Composable content
// ---------------------------------------------------------------------------

@Composable
fun UsbTetheringWidgetContent(isOn: Boolean) {
    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(ColorProvider(BaseWidget.BackgroundColor))
            .cornerRadius(16.dp)
            .clickable(onClick = actionRunCallback<UsbTetheringAction>()),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = GlanceModifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon badge — green when active, surface-dark when off
            Box(
                modifier = GlanceModifier
                    .size(40.dp)
                    .background(
                        ColorProvider(
                            if (isOn) BaseWidget.SuccessColor else BaseWidget.SurfaceColor
                        )
                    )
                    .cornerRadius(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    provider = ImageProvider(R.drawable.ic_usb_tethering),
                    contentDescription = if (isOn) "USB tethering on" else "USB tethering off",
                    modifier = GlanceModifier.size(24.dp)
                )
            }

            Spacer(modifier = GlanceModifier.height(4.dp))

            Text(
                text = "USB",
                style = TextStyle(
                    color = ColorProvider(BaseWidget.TextColor),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            )

            Text(
                text = if (isOn) "ON" else "OFF",
                style = TextStyle(
                    color = ColorProvider(
                        if (isOn) BaseWidget.SuccessColor else BaseWidget.DisabledColor
                    ),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
            )
        }
    }
}