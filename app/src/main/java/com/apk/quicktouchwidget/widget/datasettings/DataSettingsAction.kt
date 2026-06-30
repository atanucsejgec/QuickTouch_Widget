package com.apk.quicktouchwidget.widget.datasettings

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import com.apk.quicktouchwidget.core.base.BaseAction

/**
 * Action callback for the Data Settings widget.
 * Opens mobile data settings page.
 */
class DataSettingsAction : BaseAction() {

    companion object {
        private const val TAG = "DataSettingsAction"
    }

    override suspend fun onExecute(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {

        // Strategy 1: Direct Intent to Data Usage Settings
        try {
            val intent = Intent("android.settings.DATA_USAGE_SETTINGS").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                // Try to highlight mobile data toggle
                val highlightKey = "mobile_data"
                
                // AOSP / Pixel — scrolls + highlights the preference
                putExtra(":settings:fragment_args_key", highlightKey)
                
                // Samsung / LG / others
                putExtra("EXTRA_FRAGMENT_ARG_KEY", highlightKey)
                
                // Android 13+ highlight menu key
                val bundle = Bundle().apply {
                    putString(":settings:fragment_args_key", highlightKey)
                }
                putExtra(":settings:show_fragment_args", bundle)

                // Additional keys used by various ROMs
                putExtra("highlight_menu_key", highlightKey)
                putExtra("search_menu_key", highlightKey)
            }
            context.startActivity(intent)
            return
        } catch (e: Exception) {
            Log.w(TAG, "DATA_USAGE_SETTINGS not available", e)
        }

        // Strategy 2: Direct Intent to Data Roaming / Mobile Network Settings
        try {
            val intent = Intent(Settings.ACTION_DATA_ROAMING_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            return
        } catch (e: Exception) {
            Log.w(TAG, "ACTION_DATA_ROAMING_SETTINGS not available", e)
        }

        // Strategy 3: App Search Settings
        try {
            val searchIntent = Intent("android.settings.APP_SEARCH_SETTINGS").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                putExtra("query", "Mobile data")
            }
            context.startActivity(searchIntent)
            return
        } catch (e: ActivityNotFoundException) {
            Log.w(TAG, "APP_SEARCH_SETTINGS not available", e)
        }

        // Strategy 4: Generic settings search
        try {
            val searchIntent = Intent(Settings.ACTION_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                putExtra("query", "Mobile data")
                
                val highlightKey = "mobile_data"
                putExtra(":settings:fragment_args_key", highlightKey)
                putExtra("EXTRA_FRAGMENT_ARG_KEY", highlightKey)
                val bundle = Bundle().apply {
                    putString(":settings:fragment_args_key", highlightKey)
                }
                putExtra(":settings:show_fragment_args", bundle)
                
                // Add manufacturer specific keys here too
                putExtra("highlight_menu_key", highlightKey)
                putExtra("search_menu_key", highlightKey)
            }
            context.startActivity(searchIntent)
            return
        } catch (e: Exception) {
            Log.w(TAG, "Generic settings search failed", e)
        }

        // Strategy 5: Fallback to Wireless Settings
        try {
            val fallback = Intent(Settings.ACTION_WIRELESS_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(fallback)
        } catch (e: Exception) {
            Log.e(TAG, "Could not open any network settings: ${e.message}", e)
            // Final fallback to main settings
            try {
                val settingsIntent = Intent(Settings.ACTION_SETTINGS).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(settingsIntent)
            } catch (finalException: Exception) {
                Log.e(TAG, "Final fallback failed", finalException)
            }
        }
    }
}
