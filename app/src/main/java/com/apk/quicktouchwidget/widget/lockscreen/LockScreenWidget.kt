package com.apk.quicktouchwidget.widget.lockscreen

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
 * Glance widget that locks the device screen with a single tap.
 *
 * Displays one of two states:
 * - **Ready** – device admin is active; shows a lock icon with "Lock Now".
 * - **Setup Required** – device admin is not active; shows a warning icon
 *   with "Tap to setup".
 *
 * Recommended size: **1 × 1** home-screen cell.
 */
class LockScreenWidget : BaseWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            @Suppress("UNUSED_VARIABLE")
            val prefs = currentState<Preferences>()
            val isAdminActive = DeviceAdminHelper.isAdminActive(context)
            LockScreenWidgetContent(isAdminActive = isAdminActive)
        }
    }
}

/**
 * Composable content for the lock-screen widget.
 *
 * @param isAdminActive Whether the device admin is currently active.
 */
@Composable
fun LockScreenWidgetContent(isAdminActive: Boolean) {
    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(ColorProvider(BaseWidget.BackgroundColor))
            .cornerRadius(16.dp)
            .clickable(onClick = actionRunCallback<LockScreenAction>()),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = GlanceModifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isAdminActive) {
                // --- Ready state ---
                Box(
                    modifier = GlanceModifier
                        .size(48.dp)
                        .background(ColorProvider(BaseWidget.SecondaryColor))
                        .cornerRadius(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        provider = ImageProvider(R.drawable.ic_lock),
                        contentDescription = "Lock screen",
                        modifier = GlanceModifier.size(36.dp)
                    )
                }
                Spacer(modifier = GlanceModifier.height(6.dp))
                Text(
                    text = "Lock Now",
                    style = TextStyle(
                        color = ColorProvider(BaseWidget.TextColor),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                )
            } else {
                // --- Setup Required state ---
                Box(
                    modifier = GlanceModifier
                        .size(48.dp)
                        .background(ColorProvider(BaseWidget.AccentColor))
                        .cornerRadius(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        provider = ImageProvider(R.drawable.ic_warning),
                        contentDescription = "Setup required",
                        modifier = GlanceModifier.size(36.dp)
                    )
                }
                Spacer(modifier = GlanceModifier.height(4.dp))
                Text(
                    text = "Setup Required",
                    style = TextStyle(
                        color = ColorProvider(BaseWidget.WarningColor),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                )
                Spacer(modifier = GlanceModifier.height(2.dp))
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
