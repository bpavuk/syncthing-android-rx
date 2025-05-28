package com.nutomic.syncthingandroid.util.compose.permissionBoilerplate

interface BoilerplatePermissionState {
    val granted: Boolean

    fun requestPermission()
}
