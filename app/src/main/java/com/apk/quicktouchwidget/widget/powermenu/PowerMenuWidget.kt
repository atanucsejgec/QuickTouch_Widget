package com.apk.quicktouchwidget.widget.powermenu

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
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
 * Power Menu widget that opens the system power dialog.
 * Requires Accessibility Service to function.
 */
class PowerMenuWidget : BaseWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val isServiceEnabled = PowerMenuService.isServiceEnabled(context)

        provideContent {
            PowerMenuContent(isServiceEnabled = isServiceEnabled)
        }
    }
}

/**
 * Composable content for the Power Menu widget.
 *
 * @param isServiceEnabled Whether the accessibility service is enabled
 */
@Composable
private fun PowerMenuContent(isServiceEnabled: Boolean) {
    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(ColorProvider(BackgroundColor))
            .cornerRadius(16.dp)
            .clickable(onClick = actionRunCallback<PowerMenuAction>()),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = GlanceModifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                provider = ImageProvider(
                    if (isServiceEnabled) R.drawable.ic_power else R.drawable.ic_warning
                ),
                contentDescription = "Power Menu",
                modifier = GlanceModifier.size(36.dp)
            )
            Spacer(modifier = GlanceModifier.height(4.dp))
            Text(
                text = if (isServiceEnabled) "Power" else "Setup",
                style = TextStyle(
                    color = ColorProvider(if (isServiceEnabled) TextColor else WarningColor),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            )
            if (!isServiceEnabled) {
                Text(
                    text = "Tap to setup",
                    style = TextStyle(
                        color = ColorProvider(DisabledColor),
                        fontSize = 10.sp,
                        textAlign = TextAlign.Center
                    )
                )
            }
        }
    }
}

/** Access base widget colors in composable scope. */
private val BackgroundColor = BaseWidget.BackgroundColor
private val TextColor = BaseWidget.TextColor
private val WarningColor = BaseWidget.WarningColor
private val DisabledColor = BaseWidget.DisabledColor
