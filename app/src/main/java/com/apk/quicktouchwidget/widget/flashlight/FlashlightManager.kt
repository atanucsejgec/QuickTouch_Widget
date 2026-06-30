package com.apk.quicktouchwidget.widget.flashlight

import android.content.Context
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Manages flashlight (torch) operations using the Camera2 API.
 *
 * Provides thread-safe toggling of the device torch and tracks the current
 * torch state via a [CameraManager.TorchCallback]. The singleton instance
 * ensures only one torch callback is registered across the app lifecycle.
 */
object FlashlightManager {

    /** Whether the torch is currently on. */
    @Volatile
    var isTorchOn: Boolean = false
        private set

    /** Mutex to ensure thread-safe torch toggling. */
    private val mutex = Mutex()

    /** Flag to track whether the torch callback has been registered. */
    private var callbackRegistered = false

    /**
     * Torch state callback that updates [isTorchOn] whenever the system
     * reports a torch mode change.
     */
    private val torchCallback = object : CameraManager.TorchCallback() {
        override fun onTorchModeChanged(cameraId: String, enabled: Boolean) {
            isTorchOn = enabled
        }
    }

    /**
     * Registers the torch callback with the [CameraManager] so that
     * [isTorchOn] stays in sync with the actual hardware state.
     *
     * Safe to call multiple times; the callback is only registered once.
     *
     * @param context Application or widget context.
     */
    fun registerTorchCallback(context: Context) {
        if (callbackRegistered) return
        try {
            val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
            cameraManager.registerTorchCallback(torchCallback, null)
            callbackRegistered = true
        } catch (e: CameraAccessException) {
            // Camera not available – callback cannot be registered.
        } catch (e: IllegalArgumentException) {
            // Rare edge-case on some OEM ROMs.
        }
    }

    /**
     * Toggles the device flashlight on or off.
     *
     * The operation is protected by a [Mutex] to prevent concurrent toggle
     * requests from corrupting state. If the camera is unavailable or the
     * device lacks a flash unit, the exception is caught silently and the
     * torch state remains unchanged.
     *
     * @param context Application or widget context.
     * @return `true` if the torch is now on, `false` otherwise.
     */
    suspend fun toggleFlashlight(context: Context): Boolean = mutex.withLock {
        try {
            val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
            val cameraId = cameraManager.cameraIdList.firstOrNull() ?: return@withLock isTorchOn
            val newState = !isTorchOn
            cameraManager.setTorchMode(cameraId, newState)
            isTorchOn = newState
            isTorchOn
        } catch (e: CameraAccessException) {
            // Camera in use or not available.
            isTorchOn
        } catch (e: IllegalArgumentException) {
            // Invalid camera ID on some devices.
            isTorchOn
        }
    }

    /**
     * Queries the current torch state directly from the manager cache.
     *
     * @return `true` if the torch is on.
     */
    fun isOn(): Boolean = isTorchOn
}
