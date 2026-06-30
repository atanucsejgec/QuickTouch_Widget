package com.apk.quicktouchwidget.ui.components

import android.content.Context
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.apk.quicktouchwidget.R
import com.apk.quicktouchwidget.core.model.WidgetConfig
import com.apk.quicktouchwidget.ui.theme.AppTextSecondary
import com.apk.quicktouchwidget.ui.theme.CardSurface
import com.apk.quicktouchwidget.ui.theme.ErrorRed
import com.apk.quicktouchwidget.ui.theme.SuccessGreen
import com.apk.quicktouchwidget.ui.theme.WarningYellow

/**
 * Represents the status of a widget on the device.
 */
enum class WidgetStatus {
    READY, SETUP_REQUIRED, NOT_SUPPORTED
}

/**
 * Reusable widget card composable displaying widget info and status.
 *
 * @param config The widget configuration
 * @param status Current widget status
 * @param onClick Click handler for the card
 */
@Composable
fun WidgetCard(
    config: WidgetConfig,
    status: WidgetStatus,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val statusColor by animateColorAsState(
        targetValue = when (status) {
            WidgetStatus.READY -> SuccessGreen
            WidgetStatus.SETUP_REQUIRED -> WarningYellow
            WidgetStatus.NOT_SUPPORTED -> ErrorRed
        },
        label = "statusColor"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    painter = painterResource(id = config.iconRes),
                    contentDescription = stringResource(config.widgetName),
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(36.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(config.widgetName),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                // Status dot
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(statusColor)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(config.widgetDescription),
                style = MaterialTheme.typography.bodySmall,
                color = AppTextSecondary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Status label
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = when (status) {
                        WidgetStatus.READY -> stringResource(R.string.ready)
                        WidgetStatus.SETUP_REQUIRED -> stringResource(R.string.setup_required)
                        WidgetStatus.NOT_SUPPORTED -> stringResource(R.string.not_supported)
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = statusColor
                )
            }
        }
    }
}
