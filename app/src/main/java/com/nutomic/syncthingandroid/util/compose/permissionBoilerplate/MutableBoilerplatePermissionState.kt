package com.nutomic.syncthingandroid.util.compose.permissionBoilerplate

import androidx.activity.result.ActivityResultLauncher

interface MutableBoilerplatePermissionState<T> : BoilerplatePermissionState {
    override var granted: Boolean

    var launcher: ActivityResultLauncher<T>?

    fun getPermissionStatus(): Boolean

    fun refreshPermissionStatus() {
        granted = getPermissionStatus()
    }
}
