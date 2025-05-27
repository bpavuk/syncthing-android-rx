package com.nutomic.syncthingandroid.util.compose

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.nutomic.syncthingandroid.util.compose.scopedStorage.ScopedStorageContract

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

@RequiresApi(Build.VERSION_CODES.R)
@Composable
private fun PermissionLifecycleCheckerEffect(permissionState: MutableScopedStoragePermissionState) {
    val permissionCheckerObserver = LifecycleEventObserver { _, event ->
        if (event == Lifecycle.Event.ON_RESUME) {
            permissionState.refreshPermissionStatus()
        }
    }

    val lifecycle = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(lifecycle, permissionCheckerObserver) {
        lifecycle.addObserver(permissionCheckerObserver)
        onDispose {
            lifecycle.removeObserver(permissionCheckerObserver)
        }
    }
}

interface ScopedStoragePermissionState {
    val granted: Boolean

    fun requestPermission()
}

private class PreviewScopedStoragePermissionState(
    override val granted: Boolean
) : ScopedStoragePermissionState {
    override fun requestPermission() {}
}

@RequiresApi(Build.VERSION_CODES.R)
private class MutableScopedStoragePermissionState(
    private val context: Context
) : ScopedStoragePermissionState {
    override var granted: Boolean by mutableStateOf(getPermissionStatus())

    @SuppressLint("LongLogTag")
    override fun requestPermission() {
        val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
        intent.setData(("package:" + context.packageName).toUri())
        try {
            // Launch "Allow all files access?" dialog.
            launcher?.launch(context.packageName)
                ?: throw IllegalStateException("ActivityResultLauncher cannot be null")
            return
        } catch (e: ActivityNotFoundException) {
            Log.w("MutableScopedStoragePermissionState", "Request all files access not supported", e)
        }

    }

    var launcher: ActivityResultLauncher<String>? = null

    fun getPermissionStatus(): Boolean {
        return Environment.isExternalStorageManager()
    }

    fun refreshPermissionStatus() {
        granted = getPermissionStatus()
    }
}
