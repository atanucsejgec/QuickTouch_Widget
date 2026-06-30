package com.apk.quicktouchwidget.widget.volume

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
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
 * Glance widget for controlling media volume.
 *
 * Displays the current volume percentage with dedicated buttons for
 * volume-up, volume-down, and mute toggle. Tapping the centre icon
 * toggles mute.
 *
 * Recommended size: **3 × 1** home-screen cells.
 */
class VolumeWidget : BaseWidget() {

    companion object {
        /** Preferences key for the current volume percentage (0-100). */
        val VOLUME_LEVEL_KEY = intPreferencesKey("volume_level")

        /** Preferences key for the mute state. */
        val VOLUME_MUTED_KEY = booleanPreferencesKey("volume_muted")
    }

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            val prefs = currentState<Preferences>()
            val volumeLevel = prefs[VOLUME_LEVEL_KEY] ?: VolumeManager.getCurrentVolume(context)
            val isMuted = prefs[VOLUME_MUTED_KEY] ?: VolumeManager.isMuted(context)
            VolumeWidgetContent(volumeLevel = volumeLevel, isMuted = isMuted)
        }
    }
}

/**
 * Composable content for the volume widget.
 *
 * @param volumeLevel Current volume as a 0-100 percentage.
 * @param isMuted     Whether the media stream is muted.
 */
@Composable
fun VolumeWidgetContent(volumeLevel: Int, isMuted: Boolean) {
    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(ColorProvider(BaseWidget.BackgroundColor))
            .cornerRadius(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = GlanceModifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- Volume Down button ---
            Box(
                modifier = GlanceModifier
                    .size(44.dp)
                    .background(ColorProvider(BaseWidget.SurfaceColor))
                    .cornerRadius(12.dp)
                    .clickable(onClick = actionRunCallback<VolumeDownAction>()),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    provider = ImageProvider(R.drawable.ic_volume_down),
                    contentDescription = "Volume down",
                    modifier = GlanceModifier.size(28.dp)
                )
            }

            Spacer(modifier = GlanceModifier.width(8.dp))

            // --- Centre: icon + level text (tap to mute) ---
            Box(
                modifier = GlanceModifier
                    .size(width = 80.dp, height = 56.dp)
                    .background(
                        ColorProvider(
                            if (isMuted) BaseWidget.AccentColor else BaseWidget.SecondaryColor
                        )
                    )
                    .cornerRadius(12.dp)
                    .clickable(onClick = actionRunCallback<VolumeMuteAction>()),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = GlanceModifier.padding(4.dp)
                ) {
                    Image(
                        provider = ImageProvider(
                            when {
                                isMuted -> R.drawable.ic_volume_mute
                                volumeLevel > 50 -> R.drawable.ic_volume_up
                                else -> R.drawable.ic_volume_down
                            }
                        ),
                        contentDescription = when {
                            isMuted -> "Muted"
                            else -> "Volume $volumeLevel%"
                        },
                        modifier = GlanceModifier.size(28.dp)
                    )
                    Spacer(modifier = GlanceModifier.height(2.dp))
                    Text(
                        text = if (isMuted) "Muted" else "$volumeLevel%",
                        style = TextStyle(
                            color = ColorProvider(BaseWidget.TextColor),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            Spacer(modifier = GlanceModifier.width(8.dp))

            // --- Volume Up button ---
            Box(
                modifier = GlanceModifier
                    .size(44.dp)
                    .background(ColorProvider(BaseWidget.SurfaceColor))
                    .cornerRadius(12.dp)
                    .clickable(onClick = actionRunCallback<VolumeUpAction>()),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    provider = ImageProvider(R.drawable.ic_volume_up),
                    contentDescription = "Volume up",
                    modifier = GlanceModifier.size(28.dp)
                )
            }
        }
    }
}
