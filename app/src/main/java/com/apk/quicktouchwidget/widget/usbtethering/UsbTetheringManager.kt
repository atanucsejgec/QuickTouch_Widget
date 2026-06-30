package com.apk.quicktouchwidget.widget.usbtethering

import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import android.util.Log
import androidx.glance.GlanceId
import androidx.glance.appwidget.state.updateAppWidgetState

/**
 * Handles all USB tethering business logic in a single place.
 *
 * **How tethering toggle works:**
 * - API 30+ : [android.net.TetheringManager] public API (preferred, no reflection).
 * - API 24–29: Hidden `ConnectivityManager.setUsbTethering(Boolean)` via reflection.
 *   This is a grey-list API; it works on AOSP-based ROMs but may be blocked by some
 *   OEM builds. There is no fully-public alternative below API 30.
 *
 * **Reading state:**
 * - [ConnectivityManager.getTetheredIfaces] returns the list of tethered interfaces.
 *   A non-empty list containing "usb*" or "rndis*" means USB tethering is active.
 *   This method is public and available from API 24.
 */
object UsbTetheringManager {

    private const val TAG = "UsbTetheringManager"

    // USB network interface name prefixes across AOSP / OEM ROMs
    private val USB_IFACE_PREFIXES = listOf("usb", "rndis", "ncm")

    // Hidden API constants (AOSP defaults)
    private const val TETHERING_USB = 1
    private const val TETHER_ERROR_NO_ERROR = 0

    // ---------------------------------------------------------------------------
    // State reading
    // ---------------------------------------------------------------------------

    /**
     * Returns `true` if USB tethering is currently active by inspecting the
     * list of tethered network interfaces reported by [ConnectivityManager].
     */
    fun isUsbTetheringEnabled(context: Context): Boolean {
        return try {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE)
                    as ConnectivityManager
            
            // getTetheredIfaces is a hidden API. We use reflection to access it.
            val method = cm.javaClass.getMethod("getTetheredIfaces")
            @Suppress("UNCHECKED_CAST")
            val ifaces = method.invoke(cm) as? Array<String> ?: return false
            
            ifaces.any { iface ->
                USB_IFACE_PREFIXES.any { prefix -> iface.startsWith(prefix, ignoreCase = true) }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Could not read tethered interfaces: ${e.message}", e)
            false
        }
    }

    // ---------------------------------------------------------------------------
    // Toggle
    // ---------------------------------------------------------------------------

    /**
     * Toggles USB tethering and returns the **resulting** state (`true` = on).
     *
     * On API 30+ uses the public [android.net.TetheringManager]; on older APIs
     * falls back to reflection on the hidden `setUsbTethering` method.
     */
    fun toggle(context: Context): Boolean {
        val current = isUsbTetheringEnabled(context)
        val desired = !current
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            toggleViaTetheringManager(context, desired)
        } else {
            toggleViaReflection(context, desired)
        }
    }

    // ---------------------------------------------------------------------------
    // State sync helper (called on widget placement / update)
    // ---------------------------------------------------------------------------

    /**
     * Reads the real hardware state and writes it into the widget's DataStore
     * so the UI is always in sync, even after a reboot or external state change.
     */
    suspend fun syncState(context: Context, glanceId: GlanceId) {
        val isOn = isUsbTetheringEnabled(context)
        updateAppWidgetState(context, glanceId) { prefs ->
            prefs[UsbTetheringWidget.USB_TETHERING_ON_KEY] = isOn
        }
    }

    // ---------------------------------------------------------------------------
    // API 30+ implementation — TetheringManager (public API)
    // ---------------------------------------------------------------------------

    @androidx.annotation.RequiresApi(Build.VERSION_CODES.R)
    private fun toggleViaTetheringManager(context: Context, enable: Boolean): Boolean {
        return try {
            // TetheringManager is a system API, accessed via "tethering" service string
            val tm = context.getSystemService("tethering")
                ?: throw IllegalStateException("TetheringManager not available")

            if (enable) {
                // Starting tethering via TetheringManager requires complex callback objects.
                // For USB tethering, the legacy reflection method on ConnectivityManager 
                // is often more reliable and easier to call across different versions.
                toggleViaReflection(context, enable)
            } else {
                // stopTethering(int type)
                val method = tm.javaClass.getMethod("stopTethering", Int::class.javaPrimitiveType)
                method.invoke(tm, TETHERING_USB)
                Log.d(TAG, "USB tethering stopped via TetheringManager reflection")
                enable
            }
        } catch (e: Exception) {
            Log.e(TAG, "TetheringManager toggle failed: ${e.message}", e)
            // Fall back to reflection even on API 30+ if something went wrong
            toggleViaReflection(context, enable)
        }
    }

    // ---------------------------------------------------------------------------
    // API 24–29 implementation — reflection on hidden ConnectivityManager method
    // ---------------------------------------------------------------------------

    private fun toggleViaReflection(context: Context, enable: Boolean): Boolean {
        return try {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE)
                    as ConnectivityManager
            @Suppress("DiscouragedPrivateApi")
            val method = ConnectivityManager::class.java
                .getDeclaredMethod("setUsbTethering", Boolean::class.java)
            method.isAccessible = true
            val result = method.invoke(cm, enable) as? Int ?: -1
            if (result == TETHER_ERROR_NO_ERROR) {
                Log.d(TAG, "setUsbTethering($enable) succeeded via reflection")
                enable
            } else {
                Log.w(TAG, "setUsbTethering($enable) returned error code $result")
                // Return current actual state since we're unsure of the outcome
                isUsbTetheringEnabled(context)
            }
        } catch (e: NoSuchMethodException) {
            Log.e(TAG, "setUsbTethering not found — OEM may have removed it", e)
            isUsbTetheringEnabled(context)
        } catch (e: Exception) {
            Log.e(TAG, "Reflection toggle failed: ${e.message}", e)
            isUsbTetheringEnabled(context)
        }
    }
}