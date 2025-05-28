package com.nutomic.syncthingandroid.util.compose.doze

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Context.POWER_SERVICE
import android.content.Intent
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.core.net.toUri
import com.nutomic.syncthingandroid.util.compose.permissionBoilerplate.BoilerplatePermissionState
import com.nutomic.syncthingandroid.util.compose.permissionBoilerplate.MutableBoilerplatePermissionState

@RequiresApi(Build.VERSION_CODES.M)
@Composable
fun rememberDozePermissionState(): DozePermissionState {
    return rememberDozePermissionState(previewGranted = true)
}

@RequiresApi(Build.VERSION_CODES.M)
@Composable
fun rememberDozePermissionState(previewGranted: Boolean): DozePermissionState {
    return when {
        LocalInspectionMode.current -> PreviewDozePermissionState(previewGranted)
        else -> rememberMutableDozePermissionState()
    }
}

@RequiresApi(Build.VERSION_CODES.M)
@Composable
private fun rememberMutableDozePermissionState(): MutableDozePermissionState {
    val context = LocalContext.current
    val state = remember {
        MutableDozePermissionState(context)
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        state.refreshPermissionStatus()
    }

    DisposableEffect(launcher, state) {
        state.launcher = launcher
        onDispose {
            state.launcher = null
        }
    }

    return state
}

interface DozePermissionState : BoilerplatePermissionState

private class PreviewDozePermissionState(
    override val granted: Boolean
) : BoilerplatePermissionState, DozePermissionState {
    override fun requestPermission() {}
}

@RequiresApi(Build.VERSION_CODES.M)
private class MutableDozePermissionState(
    context: Context
) : MutableBoilerplatePermissionState<Intent>, DozePermissionState {

    val packageName: String = context.packageName

    val powerManager: PowerManager = context.getSystemService(POWER_SERVICE) as PowerManager

    override var granted: Boolean by mutableStateOf(getPermissionStatus())

    override var launcher: ActivityResultLauncher<Intent>? = null

    @SuppressLint("LongLogTag")
    override fun requestPermission() {
        try {
            val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
            intent.setData("package:$packageName".toUri())
            launcher?.launch(intent)
                ?: throw IllegalStateException("ActivityResultLauncher cannot be null")
            return
        } catch (e: ActivityNotFoundException) {
            Log.w("MutableDozePermissionState", "Doze not supported", e)
        }
    }

    override fun getPermissionStatus(): Boolean {
        return powerManager.isIgnoringBatteryOptimizations(packageName)
    }
}