package com.apk.quicktouchwidget.core.registry

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import com.apk.quicktouchwidget.R
import com.apk.quicktouchwidget.core.model.WidgetCategory
import com.apk.quicktouchwidget.core.model.WidgetConfig
import com.apk.quicktouchwidget.widget.flashlight.FlashlightReceiver
import com.apk.quicktouchwidget.widget.volume.VolumeReceiver
import com.apk.quicktouchwidget.widget.lockscreen.LockScreenReceiver
import com.apk.quicktouchwidget.widget.powermenu.PowerMenuReceiver
import com.apk.quicktouchwidget.widget.shortcut.ShortcutReceiver
import com.apk.quicktouchwidget.widget.datasettings.DataSettingsReceiver
import com.apk.quicktouchwidget.widget.devoptions.DevOptionsReceiver

/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║                    QUICKTOUCH WIDGET REGISTRY                   ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║                                                                  ║
 * ║  Central registry for all QuickTouch widgets.                    ║
 * ║                                                                  ║
 * ║  HOW TO ADD A NEW WIDGET:                                        ║
 * ║  ─────────────────────────                                       ║
 * ║  1. Create your widget package under widget/                     ║
 * ║     (e.g., widget/mywidget/)                                     ║
 * ║  2. Implement GlanceAppWidget + GlanceAppWidgetReceiver          ║
 * ║  3. Add drawable icon (ic_mywidget) to res/drawable/             ║
 * ║  4. Add string resources for name & description                  ║
 * ║  5. Add widget_info XML to res/xml/                              ║
 * ║  6. Register receiver in AndroidManifest.xml                     ║
 * ║  7. Add a WidgetConfig entry to the list below                   ║
 * ║                                                                  ║
 * ╚══════════════════════════════════════════════════════════════════╝
 */

/**
 * Singleton registry containing the configuration for all available widgets.
 * Acts as the single source of truth for widget discovery across the app.
 */
object WidgetRegistry {

    /**
     * The complete list of all registered widgets.
     * Each entry is a [WidgetConfig] describing the widget's identity,
     * permissions, category, and runtime availability.
     */
    val widgets: List<WidgetConfig> = listOf(

        // ── FUNCTIONAL WIDGETS ──────────────────────────────────────

        WidgetConfig(
            widgetId = "flashlight",
            widgetName = R.string.widget_flashlight_name,
            widgetDescription = R.string.widget_flashlight_desc,
            iconRes = R.drawable.ic_flashlight,
            receiverClass = FlashlightReceiver::class.java,
            requiresPermission = listOf(Manifest.permission.CAMERA),
            category = WidgetCategory.FUNCTIONAL,
            isAvailable = { context ->
                context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)
            }
        ),

        WidgetConfig(
            widgetId = "volume",
            widgetName = R.string.widget_volume_name,
            widgetDescription = R.string.widget_volume_desc,
            iconRes = R.drawable.ic_volume_up,
            receiverClass = VolumeReceiver::class.java,
            category = WidgetCategory.FUNCTIONAL
        ),

        WidgetConfig(
            widgetId = "lockscreen",
            widgetName = R.string.widget_lockscreen_name,
            widgetDescription = R.string.widget_lockscreen_desc,
            iconRes = R.drawable.ic_lock,
            receiverClass = LockScreenReceiver::class.java,
            requiresPermission = listOf("android.permission.BIND_DEVICE_ADMIN"),
            category = WidgetCategory.FUNCTIONAL
        ),

        WidgetConfig(
            widgetId = "powermenu",
            widgetName = R.string.widget_powermenu_name,
            widgetDescription = R.string.widget_powermenu_desc,
            iconRes = R.drawable.ic_power,
            receiverClass = PowerMenuReceiver::class.java,
            requiresPermission = listOf("android.permission.BIND_ACCESSIBILITY_SERVICE"),
            minAndroidVersion = Build.VERSION_CODES.LOLLIPOP,
            category = WidgetCategory.FUNCTIONAL
        ),

        // ── SHORTCUT WIDGETS ────────────────────────────────────────

        WidgetConfig(
            widgetId = "shortcut",
            widgetName = R.string.widget_shortcut_name,
            widgetDescription = R.string.widget_shortcut_desc,
            iconRes = R.drawable.ic_link,
            receiverClass = ShortcutReceiver::class.java,
            requiresPermission = listOf(Manifest.permission.INTERNET),
            category = WidgetCategory.SHORTCUT
        ),

        // ── SETTINGS WIDGETS ────────────────────────────────────────

        WidgetConfig(
            widgetId = "datasettings",
            widgetName = R.string.widget_datasettings_name,
            widgetDescription = R.string.widget_datasettings_desc,
            iconRes = R.drawable.ic_data,
            receiverClass = DataSettingsReceiver::class.java,
            category = WidgetCategory.SETTINGS
        ),

        WidgetConfig(
            widgetId = "devoptions",
            widgetName = R.string.widget_devoptions_name,
            widgetDescription = R.string.widget_devoptions_desc,
            iconRes = R.drawable.ic_dev_options,
            receiverClass = DevOptionsReceiver::class.java,
            category = WidgetCategory.SETTINGS
        )
    )
}
