package com.apk.quicktouchwidget.widget.shortcut

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.lifecycle.lifecycleScope
import com.apk.quicktouchwidget.R
import kotlinx.coroutines.launch

/**
 * Configuration Activity shown when the user places a URL Shortcut widget on the home screen.
 * Allows the user to enter a URL and optional label for each widget instance.
 */
class ShortcutConfigActivity : ComponentActivity() {

    private var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        appWidgetId = intent?.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID

        // Set CANCELED result by default so if user backs out, widget isn't added
        setResult(
            RESULT_CANCELED,
            Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        )

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }

        setContent {
            var url by remember { mutableStateOf("") }
            var label by remember { mutableStateOf("") }
            var showError by remember { mutableStateOf(false) }

            val bgColor = Color(0xFF0A0A1A)
            val surfaceColor = Color(0xFF1A1A2E)
            val accentColor = Color(0xFFE94560)
            val textColor = Color(0xFFFFFFFF)
            val secondaryText = Color(0xFFB0B0B0)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(bgColor)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(R.string.shortcut_config_title),
                    color = textColor,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(32.dp))

                OutlinedTextField(
                    value = url,
                    onValueChange = {
                        url = it
                        showError = false
                    },
                    label = { Text(stringResource(R.string.enter_url)) },
                    placeholder = { Text(stringResource(R.string.url_hint)) },
                    isError = showError,
                    supportingText = if (showError) {
                        { Text(stringResource(R.string.invalid_url), color = Color(0xFFF44336)) }
                    } else null,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = textColor,
                        unfocusedTextColor = textColor,
                        focusedBorderColor = accentColor,
                        unfocusedBorderColor = secondaryText,
                        focusedLabelColor = accentColor,
                        unfocusedLabelColor = secondaryText,
                        cursorColor = accentColor,
                        focusedPlaceholderColor = secondaryText,
                        unfocusedPlaceholderColor = secondaryText
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = label,
                    onValueChange = { label = it },
                    label = { Text(stringResource(R.string.enter_label)) },
                    placeholder = { Text(stringResource(R.string.label_hint)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = textColor,
                        unfocusedTextColor = textColor,
                        focusedBorderColor = accentColor,
                        unfocusedBorderColor = secondaryText,
                        focusedLabelColor = accentColor,
                        unfocusedLabelColor = secondaryText,
                        cursorColor = accentColor,
                        focusedPlaceholderColor = secondaryText,
                        unfocusedPlaceholderColor = secondaryText
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(32.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    TextButton(
                        onClick = { finish() },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = stringResource(R.string.cancel),
                            color = secondaryText,
                            fontSize = 16.sp
                        )
                    }

                    Button(
                        onClick = {
                            if (url.isBlank()) {
                                showError = true
                            } else {
                                saveAndFinish(url.trim(), label.trim())
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.save),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }

    /**
     * Saves the URL and label to the widget's state, updates it, and finishes with OK result.
     */
    private fun saveAndFinish(url: String, label: String) {
        lifecycleScope.launch {
            try {
                val glanceId = GlanceAppWidgetManager(this@ShortcutConfigActivity)
                    .getGlanceIdBy(appWidgetId)

                updateAppWidgetState(this@ShortcutConfigActivity, glanceId) { prefs ->
                    prefs[stringPreferencesKey("shortcut_url")] = url
                    prefs[stringPreferencesKey("shortcut_label")] = label
                }

                ShortcutWidget().update(this@ShortcutConfigActivity, glanceId)

                setResult(
                    RESULT_OK,
                    Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                )
            } catch (e: Exception) {
                android.util.Log.e("ShortcutConfig", "Failed to save widget config", e)
            }
            finish()
        }
    }
}
