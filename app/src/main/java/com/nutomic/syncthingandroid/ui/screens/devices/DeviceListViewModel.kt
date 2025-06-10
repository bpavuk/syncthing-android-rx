package com.nutomic.syncthingandroid.ui.screens.devices

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutomic.syncthingandroid.ui.common.device.DeviceCardState
import com.nutomic.syncthingandroid.ui.common.device.DeviceCardSyncState
import com.nutomic.syncthingandroid.util.ConfigRouterKt
import com.nutomic.syncthingandroid.util.ConfigXml
import kotlinx.coroutines.launch

interface DeviceListViewModel {
    val devices: List<DeviceCardState>

    fun updateDevices()
}

class DeviceListViewModelImpl(
    private val configRouter: ConfigRouterKt,
) : DeviceListViewModel, ViewModel() {

    override var devices: List<DeviceCardState> by mutableStateOf(emptyList())
        private set

    override fun updateDevices() {
        viewModelScope.launch {
            try {
                devices = configRouter.loadDevices().map { device ->
                    DeviceCardState(
                        syncState = if (device.paused) {
                            DeviceCardSyncState.Paused
                        } else {
                            DeviceCardSyncState.UpToDate
                        },
                        label = device.name,
                        deviceID = device.deviceID,
                    )
                }
            } catch (e: ConfigXml.OpenConfigException) {
                Log.e(
                    TAG,
                    "Failed to parse existing config. You will need support from here...",
                    e
                )
                devices = emptyList() // Ensure devices is in a consistent state on error
            }
        }
    }

    @Suppress("PrivatePropertyName")
    private val TAG = "DeviceListViewModelImpl"
}
