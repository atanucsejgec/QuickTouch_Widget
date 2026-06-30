package com.apk.quicktouchwidget.widget.volume

import android.content.Context
import android.media.AudioManager

/**
 * Manages media volume operations via [AudioManager].
 *
 * All operations target [AudioManager.STREAM_MUSIC]. Volume values
 * exposed externally are normalised to a **0–100 percentage** for
 * consistent UI display regardless of the device's raw step count.
 */
object VolumeManager {

    /**
     * Returns the [AudioManager] system service.
     *
     * @param context Any valid context.
     */
    private fun audioManager(context: Context): AudioManager =
        context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    /**
     * Returns the maximum volume index for [AudioManager.STREAM_MUSIC].
     *
     * @param context Any valid context.
     * @return Maximum raw volume index.
     */
    fun getMaxVolume(context: Context): Int {
        return try {
            audioManager(context).getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        } catch (e: Exception) {
            15 // Sensible fallback for most devices.
        }
    }

    /**
     * Returns the current media volume as a percentage (0–100).
     *
     * @param context Any valid context.
     * @return Current volume percentage.
     */
    fun getCurrentVolume(context: Context): Int {
        return try {
            val am = audioManager(context)
            val current = am.getStreamVolume(AudioManager.STREAM_MUSIC)
            val max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            if (max > 0) (current * 100) / max else 0
        } catch (e: Exception) {
            0
        }
    }

    /**
     * Increases the media volume by one step.
     *
     * @param context Any valid context.
     */
    fun volumeUp(context: Context) {
        try {
            audioManager(context).adjustStreamVolume(
                AudioManager.STREAM_MUSIC,
                AudioManager.ADJUST_RAISE,
                0 // No UI flag – the widget itself is the UI.
            )
        } catch (e: SecurityException) {
            // Do-Not-Disturb or policy restriction.
        } catch (e: Exception) {
            // Unexpected failure – swallow to keep widget stable.
        }
    }

    /**
     * Decreases the media volume by one step.
     *
     * @param context Any valid context.
     */
    fun volumeDown(context: Context) {
        try {
            audioManager(context).adjustStreamVolume(
                AudioManager.STREAM_MUSIC,
                AudioManager.ADJUST_LOWER,
                0
            )
        } catch (e: SecurityException) {
            // Do-Not-Disturb or policy restriction.
        } catch (e: Exception) {
            // Unexpected failure.
        }
    }

    /**
     * Toggles mute for the media stream.
     *
     * Uses [AudioManager.adjustStreamVolume] with [AudioManager.ADJUST_TOGGLE_MUTE]
     * on API 23+ (our minSdk is 24, so this is always available).
     *
     * @param context Any valid context.
     */
    fun toggleMute(context: Context) {
        try {
            audioManager(context).adjustStreamVolume(
                AudioManager.STREAM_MUSIC,
                AudioManager.ADJUST_TOGGLE_MUTE,
                0
            )
        } catch (e: SecurityException) {
            // Do-Not-Disturb or policy restriction.
        } catch (e: Exception) {
            // Unexpected failure.
        }
    }

    /**
     * Checks whether the media stream is currently muted.
     *
     * @param context Any valid context.
     * @return `true` if the media stream is muted.
     */
    fun isMuted(context: Context): Boolean {
        return try {
            audioManager(context).isStreamMute(AudioManager.STREAM_MUSIC)
        } catch (e: Exception) {
            false
        }
    }
}
