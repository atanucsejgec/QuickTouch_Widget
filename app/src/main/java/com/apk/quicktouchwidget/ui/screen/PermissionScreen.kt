package com.apk.quicktouchwidget.ui.screen

import android.Manifest
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.apk.quicktouchwidget.R
import com.apk.quicktouchwidget.admin.MyDeviceAdminReceiver
import com.apk.quicktouchwidget.core.util.PermissionChecker
import com.apk.quicktouchwidget.service.PowerMenuAccessibilityService
import com.apk.quicktouchwidget.ui.theme.AppBackground
import com.apk.quicktouchwidget.ui.theme.CardSurface
import com.apk.quicktouchwidget.ui.theme.ErrorRed
import com.apk.quicktouchwidget.ui.theme.SuccessGreen

/**
 * Permission management screen.
 * Lists all permissions needed by widgets with grant/setup buttons.
 *
 * @param onBack Callback for back navigation
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PermissionScreen(onBack: () -> Unit) {
    val context = LocalContext.current

    // Track permission states
    var cameraGranted by remember {
        mutableStateOf(PermissionChecker.hasPermission(context, Manifest.permission.CAMERA))
    }
    var adminActive by remember {
        mutableStateOf(PermissionChecker.isDeviceAdminActive(context))
    }
    var accessibilityEnabled by remember {
        mutableStateOf(
            PermissionChecker.isAccessibilityServiceEnabled(
                context, PowerMenuAccessibilityService::class.java
            )
        )
    }

    // Camera permission launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        cameraGranted = granted
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = {
                Text(
                    text = stringResource(R.string.permissions_title),
                    style = MaterialTheme.typography.headlineMedium
                )
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = AppBackground,
                titleContentColor = MaterialTheme.colorScheme.onSurface
            )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Camera Permission
            PermissionItem(
                title = "Camera (Flashlight)",
                description = stringResource(R.string.camera_permission_reason),
                isGranted = cameraGranted,
                onGrant = { cameraLauncher.launch(Manifest.permission.CAMERA) },
                onOpenSettings = { openAppSettings(context) }
            )

            // Device Admin
            PermissionItem(
                title = "Device Admin (Lock Screen)",
                description = stringResource(R.string.admin_permission_reason),
                isGranted = adminActive,
                onGrant = {
                    val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN).apply {
                        putExtra(
                            DevicePolicyManager.EXTRA_DEVICE_ADMIN,
                            MyDeviceAdminReceiver.getComponentName(context)
                        )
                        putExtra(
                            DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                            context.getString(R.string.device_admin_description)
                        )
                    }
                    context.startActivity(intent)
                },
                onOpenSettings = { openAppSettings(context) },
                steps = listOf(
                    stringResource(R.string.step_1_admin),
                    stringResource(R.string.step_2_admin)
                )
            )

            // Accessibility Service
            PermissionItem(
                title = "Accessibility Service (Power Menu)",
                description = stringResource(R.string.accessibility_permission_reason),
                isGranted = accessibilityEnabled,
                onGrant = {
                    val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(intent)
                },
                onOpenSettings = {
                    val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(intent)
                },
                steps = listOf(
                    stringResource(R.string.step_1_accessibility),
                    stringResource(R.string.step_2_accessibility),
                    stringResource(R.string.step_3_accessibility)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Refresh hint
            Text(
                text = "Return to this screen after granting permissions to see updated status.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }
    }
}

/**
 * A single permission item card with status, description, and action button.
 */
@Composable
private fun PermissionItem(
    title: String,
    description: String,
    isGranted: Boolean,
    onGrant: () -> Unit,
    onOpenSettings: () -> Unit,
    steps: List<String> = emptyList()
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = if (isGranted) Icons.Filled.CheckCircle else Icons.Filled.Warning,
                    contentDescription = null,
                    tint = if (isGranted) SuccessGreen else ErrorRed,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = if (isGranted) stringResource(R.string.permission_granted)
                        else stringResource(R.string.permission_denied),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isGranted) SuccessGreen else ErrorRed
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (steps.isNotEmpty() && !isGranted) {
                Spacer(modifier = Modifier.height(8.dp))
                steps.forEach { step ->
                    Text(
                        text = "• $step",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 8.dp, top = 2.dp)
                    )
                }
            }

            if (!isGranted) {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onGrant,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.grant_permission))
                }
            }
        }
    }
}

/** Opens the app's system settings page. */
private fun openAppSettings(context: Context) {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", context.packageName, null)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    context.startActivity(intent)
}
