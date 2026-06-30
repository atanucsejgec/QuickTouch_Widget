# Prompt: Add a New Widget to QuickTouchWidget

> **How to use:** Copy everything below this line and paste it into a new AI chat. Replace the `[PLACEHOLDERS]` at the bottom with your actual widget details.

---

## INSTRUCTIONS FOR AI

I need you to add a new Android home screen widget to my existing **QuickTouchWidget** project. The project uses **Jetpack Glance** for widgets, **Kotlin**, **Jetpack Compose** for the app UI, and **Hilt** for DI. Below is the complete architecture, patterns, and all existing files you may need to modify. **Only create the new files needed and show the exact modifications to existing files.**

---

## PROJECT ARCHITECTURE

```
Package: com.apk.quicktouchwidget
Min SDK: 24 | Target SDK: 36 | Compile SDK: 37

app/src/main/
├── AndroidManifest.xml              ← ADD receiver entry here
├── java/com/apk/quicktouchwidget/
│   ├── MainActivity.kt
│   ├── QuickTouchApp.kt
│   ├── core/
│   │   ├── base/
│   │   │   ├── BaseWidget.kt        ← Extend this for widget
│   │   │   └── BaseAction.kt        ← Extend this for action
│   │   ├── model/
│   │   │   └── WidgetConfig.kt      ← Data model (DO NOT MODIFY)
│   │   ├── registry/
│   │   │   └── WidgetRegistry.kt    ← ADD widget config entry here
│   │   └── util/
│   │       ├── PermissionChecker.kt
│   │       └── ServiceChecker.kt
│   ├── widget/
│   │   ├── flashlight/              ← Example: 4 files (Widget, Action, Manager, Receiver)
│   │   ├── volume/                  ← Example: 6 files (Widget, Actions×3, Manager, Receiver)
│   │   ├── lockscreen/              ← Example: 4 files (Widget, Action, Helper, Receiver)
│   │   ├── powermenu/               ← Example: 4 files (Widget, Action, Service, Receiver)
│   │   ├── shortcut/                ← Example: 5 files (Widget, Action, Config, Manager, Receiver)
│   │   ├── datasettings/            ← Example: 3 files (Widget, Action, Receiver) — SIMPLEST
│   │   └── devoptions/              ← Example: 3 files (Widget, Action, Receiver) — SIMPLEST
│   ├── ui/
│   │   ├── screen/
│   │   │   ├── HomeScreen.kt        ← Auto-discovers widgets from registry (NO CHANGES NEEDED)
│   │   │   ├── PermissionScreen.kt  ← MAY need changes if new permission type
│   │   │   └── SettingsScreen.kt
│   │   ├── components/
│   │   │   ├── WidgetCard.kt        ← Renders from WidgetConfig (NO CHANGES NEEDED)
│   │   │   └── PermissionBanner.kt
│   │   └── theme/
│   ├── di/
│   │   └── AppModule.kt
│   ├── service/
│   │   └── PowerMenuAccessibilityService.kt
│   └── admin/
│       └── MyDeviceAdminReceiver.kt
└── res/
    ├── drawable/                     ← ADD icon drawable here
    ├── layout/
    │   └── widget_loading.xml        ← Shared loading layout (DO NOT MODIFY)
    ├── values/
    │   ├── strings.xml               ← ADD string resources here
    │   ├── colors.xml
    │   └── dimens.xml
    └── xml/                          ← ADD widget_info XML here
```

---

## KEY DESIGN PRINCIPLE: REGISTRY-DRIVEN

The app uses a **registry pattern**. The `HomeScreen` reads all widgets from `WidgetRegistry.widgets` and renders them automatically. **You do NOT need to touch HomeScreen.kt, WidgetCard.kt, or any UI screen** (unless the widget needs a brand-new permission type not already handled).

---

## BASE CLASSES (DO NOT MODIFY — EXTEND THESE)

### BaseWidget.kt
```kotlin
package com.apk.quicktouchwidget.core.base

import androidx.compose.ui.graphics.Color
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.state.PreferencesGlanceStateDefinition

abstract class BaseWidget : GlanceAppWidget() {
    override val stateDefinition = PreferencesGlanceStateDefinition

    companion object {
        val BackgroundColor = Color(0xFF1A1A2E)
        val AccentColor = Color(0xFFE94560)
        val TextColor = Color(0xFFFFFFFF)
        val SurfaceColor = Color(0xFF16213E)
        val SecondaryColor = Color(0xFF0F3460)
        val SuccessColor = Color(0xFF4CAF50)
        val DisabledColor = Color(0xFF757575)
        val WarningColor = Color(0xFFFFC107)
    }
}
```

### BaseAction.kt
```kotlin
package com.apk.quicktouchwidget.core.base

import android.content.Context
import android.util.Log
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback

abstract class BaseAction : ActionCallback {
    companion object { private const val TAG = "QuickTouchAction" }

    abstract suspend fun onExecute(
        context: Context, glanceId: GlanceId, parameters: ActionParameters
    )

    override suspend fun onAction(
        context: Context, glanceId: GlanceId, parameters: ActionParameters
    ) {
        try { onExecute(context, glanceId, parameters) }
        catch (e: Exception) { Log.e(TAG, "${this::class.simpleName} action failed: ${e.message}", e) }
    }
}
```

### WidgetConfig.kt (Data Model — DO NOT MODIFY)
```kotlin
package com.apk.quicktouchwidget.core.model

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.glance.appwidget.GlanceAppWidgetReceiver

enum class WidgetCategory { FUNCTIONAL, SHORTCUT, SETTINGS }

data class WidgetConfig(
    val widgetId: String,
    @StringRes val widgetName: Int,
    @StringRes val widgetDescription: Int,
    @DrawableRes val iconRes: Int,
    val receiverClass: Class<out GlanceAppWidgetReceiver>,
    val requiresPermission: List<String> = emptyList(),
    val minAndroidVersion: Int = android.os.Build.VERSION_CODES.N,
    val category: WidgetCategory = WidgetCategory.FUNCTIONAL,
    val isAvailable: (Context) -> Boolean = { true }
)
```

---

## REFERENCE EXAMPLE: Simple Widget (DataSettings — 3 files)

### 1. DataSettingsWidget.kt
```kotlin
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
import androidx.glance.layout.*
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.apk.quicktouchwidget.R
import com.apk.quicktouchwidget.core.base.BaseWidget

class DataSettingsWidget : BaseWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent { DataSettingsContent() }
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
```

### 2. DataSettingsAction.kt
```kotlin
package com.apk.quicktouchwidget.widget.datasettings

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.util.Log
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import com.apk.quicktouchwidget.core.base.BaseAction

class DataSettingsAction : BaseAction() {
    companion object { private const val TAG = "DataSettingsAction" }

    override suspend fun onExecute(
        context: Context, glanceId: GlanceId, parameters: ActionParameters
    ) {
        try {
            val intent = Intent(Settings.ACTION_DATA_ROAMING_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.w(TAG, "ACTION_DATA_ROAMING_SETTINGS not available, trying fallback", e)
            try {
                val fallback = Intent(Settings.ACTION_WIRELESS_SETTINGS).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(fallback)
            } catch (e2: Exception) {
                Log.e(TAG, "Could not open any network settings: ${e2.message}", e2)
            }
        }
    }
}
```

### 3. DataSettingsReceiver.kt
```kotlin
package com.apk.quicktouchwidget.widget.datasettings

import androidx.glance.appwidget.GlanceAppWidgetReceiver

class DataSettingsReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget = DataSettingsWidget()
}
```

---

## REFERENCE EXAMPLE: Stateful Widget (Flashlight — 4 files)

### 1. FlashlightWidget.kt (uses state via Preferences)
```kotlin
package com.apk.quicktouchwidget.widget.flashlight

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
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
import androidx.glance.layout.*
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.apk.quicktouchwidget.R
import com.apk.quicktouchwidget.core.base.BaseWidget

class FlashlightWidget : BaseWidget() {
    companion object {
        val FLASHLIGHT_ON_KEY = booleanPreferencesKey("flashlight_on")
    }

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        FlashlightManager.registerTorchCallback(context)
        provideContent {
            val prefs = currentState<Preferences>()
            val isOn = prefs[FLASHLIGHT_ON_KEY] ?: false
            FlashlightWidgetContent(isOn = isOn)
        }
    }
}

@Composable
fun FlashlightWidgetContent(isOn: Boolean) {
    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(ColorProvider(BaseWidget.BackgroundColor))
            .cornerRadius(16.dp)
            .clickable(onClick = actionRunCallback<FlashlightAction>()),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = GlanceModifier.fillMaxWidth().padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = GlanceModifier
                    .size(48.dp)
                    .background(ColorProvider(if (isOn) BaseWidget.SuccessColor else BaseWidget.SurfaceColor))
                    .cornerRadius(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    provider = ImageProvider(if (isOn) R.drawable.ic_flashlight else R.drawable.ic_flashlight_off),
                    contentDescription = if (isOn) "Flashlight on" else "Flashlight off",
                    modifier = GlanceModifier.size(36.dp)
                )
            }
            Spacer(modifier = GlanceModifier.width(12.dp))
            Column {
                Text("Flashlight", style = TextStyle(
                    color = ColorProvider(BaseWidget.TextColor), fontSize = 14.sp, fontWeight = FontWeight.Bold
                ))
                Spacer(modifier = GlanceModifier.height(2.dp))
                Text(if (isOn) "ON" else "OFF", style = TextStyle(
                    color = ColorProvider(if (isOn) BaseWidget.SuccessColor else BaseWidget.DisabledColor),
                    fontSize = 12.sp, fontWeight = FontWeight.Medium
                ))
            }
        }
    }
}
```

### 2. FlashlightAction.kt (updates widget state)
```kotlin
package com.apk.quicktouchwidget.widget.flashlight

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.state.updateAppWidgetState
import com.apk.quicktouchwidget.core.base.BaseAction

class FlashlightAction : BaseAction() {
    override suspend fun onExecute(
        context: Context, glanceId: GlanceId, parameters: ActionParameters
    ) {
        val isOn = FlashlightManager.toggleFlashlight(context)
        updateAppWidgetState(context, glanceId) { prefs ->
            prefs[FlashlightWidget.FLASHLIGHT_ON_KEY] = isOn
        }
        FlashlightWidget().update(context, glanceId)
    }
}
```

### 3. FlashlightReceiver.kt
```kotlin
package com.apk.quicktouchwidget.widget.flashlight

import androidx.glance.appwidget.GlanceAppWidgetReceiver

class FlashlightReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget = FlashlightWidget()
}
```

### 4. FlashlightManager.kt (business logic singleton)
```kotlin
// Separate business logic class — create one if your widget needs
// complex state management, hardware access, or async operations
```

---

## EXISTING FILES YOU MUST MODIFY

### 1. WidgetRegistry.kt — Add config entry

**Current file location:** `app/src/main/java/com/apk/quicktouchwidget/core/registry/WidgetRegistry.kt`

Add a new `WidgetConfig(...)` to the `widgets` list under the correct category section. Add the import for your new Receiver class.

**Categories:**
- `WidgetCategory.FUNCTIONAL` — Widgets that perform a device function
- `WidgetCategory.SHORTCUT` — Widgets that open URLs or apps
- `WidgetCategory.SETTINGS` — Widgets that open system settings pages

### 2. AndroidManifest.xml — Register receiver

**Add inside `<application>` block, under the Widget Receivers section:**
```xml
<!-- [Your Widget Name] Widget -->
<receiver
    android:name=".widget.[yourpackage].[YourWidget]Receiver"
    android:exported="true">
    <intent-filter>
        <action android:name="android.appwidget.action.APPWIDGET_UPDATE" />
    </intent-filter>
    <meta-data
        android:name="android.appwidget.provider"
        android:resource="@xml/[yourwidget]_widget_info" />
</receiver>
```

### 3. strings.xml — Add name and description strings

**Add under the `<!-- Widget Names -->` and `<!-- Widget Descriptions -->` sections:**
```xml
<string name="widget_[yourwidget]_name">Your Widget Name</string>
<string name="widget_[yourwidget]_desc">Brief description of what it does</string>
```

Also add any widget-specific state strings under `<!-- Widget States -->`.

### 4. PermissionScreen.kt — ONLY IF new permission type

**You ONLY need to modify this file if your widget requires a permission type NOT already handled.** Currently handled:
- `Manifest.permission.CAMERA` — Runtime permission dialog
- `"android.permission.BIND_DEVICE_ADMIN"` — Device admin activation
- `"android.permission.BIND_ACCESSIBILITY_SERVICE"` — Accessibility settings
- `Manifest.permission.INTERNET` — Auto-granted (no UI needed)

If your widget uses one of these existing permissions, **no changes needed** to PermissionScreen.

### 5. HomeScreen.kt — ONLY IF new permission type

The `getWidgetStatus()` function checks permissions. If you add a new permission type not in the existing `when` block, you must add a new branch. For existing permission types, **no changes needed**.

---

## NEW FILES TO CREATE

### 1. Widget package: `app/src/main/java/com/apk/quicktouchwidget/widget/[yourwidget]/`

At minimum, create 3 files:
- `[YourWidget]Widget.kt` — Extends `BaseWidget`, implements `provideGlance`
- `[YourWidget]Action.kt` — Extends `BaseAction`, implements `onExecute`
- `[YourWidget]Receiver.kt` — Extends `GlanceAppWidgetReceiver`, provides the widget instance

If your widget has complex logic, also create:
- `[YourWidget]Manager.kt` — Business logic singleton

### 2. Widget info XML: `app/src/main/res/xml/[yourwidget]_widget_info.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<appwidget-provider xmlns:android="http://schemas.android.com/apk/res/android"
    android:initialLayout="@layout/widget_loading"
    android:minWidth="110dp"
    android:minHeight="40dp"
    android:resizeMode="horizontal|vertical"
    android:targetCellWidth="2"
    android:targetCellHeight="1"
    android:updatePeriodMillis="0"
    android:widgetCategory="home_screen"
    android:description="@string/widget_[yourwidget]_desc" />
```

> **Size guide:** 1×1 = `minWidth="40dp" minHeight="40dp"`, 2×1 = `minWidth="110dp" minHeight="40dp"`, 2×2 = `minWidth="110dp" minHeight="110dp"`

### 3. Icon drawable: `app/src/main/res/drawable/ic_[yourwidget].xml`

Create a vector drawable icon. Use 24dp viewport.

---

## COMPLETE CHECKLIST

- [ ] Create `widget/[name]/[Name]Widget.kt` extending `BaseWidget`
- [ ] Create `widget/[name]/[Name]Action.kt` extending `BaseAction`
- [ ] Create `widget/[name]/[Name]Receiver.kt` extending `GlanceAppWidgetReceiver`
- [ ] Create `widget/[name]/[Name]Manager.kt` (if needed for complex logic)
- [ ] Create `res/xml/[name]_widget_info.xml`
- [ ] Create `res/drawable/ic_[name].xml` icon
- [ ] Add `widget_[name]_name` and `widget_[name]_desc` to `res/values/strings.xml`
- [ ] Add any widget state strings to `res/values/strings.xml`
- [ ] Add receiver entry to `AndroidManifest.xml`
- [ ] Add `WidgetConfig` entry to `WidgetRegistry.kt` (+ import for Receiver)
- [ ] Add permission to `AndroidManifest.xml` `<uses-permission>` (if new permission needed)
- [ ] Update `PermissionScreen.kt` (ONLY if new permission type)
- [ ] Update `HomeScreen.kt` `getWidgetStatus()` (ONLY if new permission type)

---

## WHAT I WANT YOU TO DO

Create a new widget with the following details:

- **Widget Name:** `[REPLACE: e.g., Bluetooth]`
- **Widget ID:** `[REPLACE: e.g., bluetooth]`
- **Package Name:** `[REPLACE: e.g., bluetooth]`
- **Category:** `[REPLACE: FUNCTIONAL / SHORTCUT / SETTINGS]`
- **What it does when tapped:** `[REPLACE: e.g., Toggles Bluetooth on/off]`
- **Widget size:** `[REPLACE: e.g., 2×1 or 1×1]`
- **Has state (ON/OFF toggle)?:** `[REPLACE: Yes/No]`
- **Permissions needed:** `[REPLACE: e.g., android.permission.BLUETOOTH_CONNECT or None]`
- **Minimum Android version:** `[REPLACE: e.g., API 24 (default) or API 31]`
- **Device check needed:** `[REPLACE: e.g., PackageManager.FEATURE_BLUETOOTH or None]`

**Please provide:**
1. All NEW files with complete code
2. For each EXISTING file that needs modification, show ONLY the specific changes (what to add and where), not the entire file
3. Use the exact same coding patterns, doc-comment style, and theme colors as the examples above
