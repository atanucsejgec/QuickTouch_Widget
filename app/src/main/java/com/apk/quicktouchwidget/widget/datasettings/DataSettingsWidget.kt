package com.apk.quicktouchwidget.widget.datasettings

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.clickable
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
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
 * Data Settings shortcut widget.
 * Opens the mobile data settings page when tapped.
 */
class DataSettingsWidget : BaseWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            DataSettingsContent()
        }
    }
}

@Composable
private fun DataSettingsContent() {
    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(ColorProvider(BaseWidget.BackgroundColor))
            .cornerRadius(16.dp)
            .clickable(onClick = actionRunCallback<DataSettingsAction>()),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = GlanceModifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                provider = ImageProvider(R.drawable.ic_data),
                contentDescription = "Data Settings",
                modifier = GlanceModifier.size(36.dp)
            )
            Spacer(modifier = GlanceModifier.height(4.dp))
            Text(
                text = "Data",
                style = TextStyle(
                    color = ColorProvider(BaseWidget.TextColor),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            )
        }
    }
}
