package com.apk.quicktouchwidget.widget.shortcut

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log

/**
 * Manages URL opening logic for the Shortcut widget.
 * Handles browser and YouTube app detection.
 */
object ShortcutManager {

    private const val TAG = "ShortcutManager"

    /**
     * Opens the given URL. Detects YouTube URLs and tries the YouTube app first.
     *
     * @param context Application context
     * @param url The URL to open
     * @return true if the URL was opened successfully
     */
    fun openUrl(context: Context, url: String): Boolean {
        return try {
            val formattedUrl = formatUrl(url)
            val uri = Uri.parse(formattedUrl)
            val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            // Try YouTube app for YouTube URLs
            if (isYouTubeUrl(formattedUrl)) {
                try {
                    intent.setPackage("com.google.android.youtube")
                    context.startActivity(intent)
                    return true
                } catch (e: ActivityNotFoundException) {
                    Log.d(TAG, "YouTube app not found, falling back to browser")
                    intent.setPackage(null)
                }
            }

            context.startActivity(intent)
            true
        } catch (e: ActivityNotFoundException) {
            Log.e(TAG, "No app found to handle URL: $url", e)
            false
        } catch (e: Exception) {
            Log.e(TAG, "Failed to open URL: $url - ${e.message}", e)
            false
        }
    }

    /**
     * Ensures the URL has a proper scheme.
     */
    private fun formatUrl(url: String): String {
        return if (!url.startsWith("http://") && !url.startsWith("https://")) {
            "https://$url"
        } else {
            url
        }
    }

    /**
     * Checks if the URL is a YouTube URL.
     */
    private fun isYouTubeUrl(url: String): Boolean {
        return url.contains("youtube.com") ||
                url.contains("youtu.be") ||
                url.contains("youtube-nocookie.com")
    }
}
