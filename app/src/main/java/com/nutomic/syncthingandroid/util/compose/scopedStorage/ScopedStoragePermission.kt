package com.nutomic.syncthingandroid.util.compose.scopedStorage

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import com.nutomic.syncthingandroid.util.compose.permissionBoilerplate.BoilerplatePermissionState
import com.nutomic.syncthingandroid.util.compose.permissionBoilerplate.MutableBoilerplatePermissionState
import com.nutomic.syncthingandroid.util.compose.permissionBoilerplate.PermissionLifecycleCheckerEffect

@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun rememberScopedStoragePermissionState(): ScopedStoragePermissionState {
    return rememberScopedStoragePermissionState(previewGranted = true)
}

@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun rememberScopedStoragePermissionState(previewGranted: Boolean): ScopedStoragePermissionState {
    return when {
        LocalInspectionMode.current -> PreviewScopedStoragePermissionState(previewGranted)
        else -> rememberMutableScopedStoragePermissionState()
    }
}

@RequiresApi(Build.VERSION_CODES.R)
@Composable
private fun rememberMutableScopedStoragePermissionState(): MutableScopedStoragePermissionState {
    val context = LocalContext.current
    val permissionState = remember {
        MutableScopedStoragePermissionState(context)
    }

    PermissionLifecycleCheckerEffect(permissionState)

    val launcher = rememberLauncherForActivityResult(
        contract = ScopedStorageContract()
    ) {
        permissionState.refreshPermissionStatus()
    }

    DisposableEffect(launcher, permissionState) {
        permissionState.launcher = launcher
        onDispose {
            permissionState.launcher = null
        }
    }

    return permissionState
}

interface ScopedStoragePermissionState : BoilerplatePermissionState

private class PreviewScopedStoragePermissionState(
    override val granted: Boolean
) : ScopedStoragePermissionState {
    override fun requestPermission() {}
}

@RequiresApi(Build.VERSION_CODES.R)
private class MutableScopedStoragePermissionState(
    private val context: Context
) : ScopedStoragePermissionState, MutableBoilerplatePermissionState<String>() {
    @SuppressLint("LongLogTag")
    override fun requestPermission() {
        try {
            // Launch "Allow all files access?" dialog.
            launcher?.launch(context.packageName)
                ?: throw IllegalStateException("ActivityResultLauncher cannot be null")
            return
        } catch (e: ActivityNotFoundException) {
            Log.w("MutableScopedStoragePermissionState", "Request all files access not supported", e)
        }

    }

    override fun getPermissionStatus(): Boolean {
        return Environment.isExternalStorageManager()
    }
}
