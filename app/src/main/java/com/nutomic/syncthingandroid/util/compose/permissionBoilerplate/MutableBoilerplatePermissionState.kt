package com.nutomic.syncthingandroid.util.compose.permissionBoilerplate

import androidx.activity.result.ActivityResultLauncher
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

abstract class MutableBoilerplatePermissionState<T> : BoilerplatePermissionState {
    override var granted: Boolean by mutableStateOf(getPermissionStatus())

    var launcher: ActivityResultLauncher<T>? = null

    abstract fun getPermissionStatus(): Boolean

    open fun refreshPermissionStatus() {
        granted = getPermissionStatus()
    }
}
