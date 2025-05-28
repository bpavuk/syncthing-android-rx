package com.nutomic.syncthingandroid.util.compose.permissionBoilerplate

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner

@Composable
fun <T> PermissionLifecycleCheckerEffect(permissionState: MutableBoilerplatePermissionState<T>) {
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
