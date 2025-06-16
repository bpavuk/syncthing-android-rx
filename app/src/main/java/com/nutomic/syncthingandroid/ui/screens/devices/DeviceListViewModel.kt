package com.nutomic.syncthingandroid.ui.screens.devices

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutomic.syncthingandroid.util.ConfigXml
import com.nutomic.syncthingandroid.util.configkt.ConfigRouterKt
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import syncthingrest.model.device.DeviceID

sealed interface DeviceListUIState {
    data object Loading : DeviceListUIState
    data object FailedToLoad : DeviceListUIState
    data class Success(
        val deviceIDs: List<DeviceID>
    ) : DeviceListUIState
}

interface DeviceListViewModel {
    val uiState: StateFlow<DeviceListUIState>

    fun updateDevices()
}

class DeviceListViewModelImpl(
    private val configRouter: ConfigRouterKt,
) : DeviceListViewModel, ViewModel() {

    private val _uiState: MutableStateFlow<DeviceListUIState> =
        MutableStateFlow(DeviceListUIState.Loading)
    override val uiState: StateFlow<DeviceListUIState> = _uiState.asStateFlow()

    override fun updateDevices() {
        viewModelScope.launch {
            try {
                _uiState.value = DeviceListUIState.Success(
                    deviceIDs = configRouter.devices.loadDevices().map { it.deviceID }
                )
            } catch (e: ConfigXml.OpenConfigException) {
                Log.e(
                    TAG,
                    "Failed to parse existing config. You will need support from here...",
                    e
                )
                _uiState.value = DeviceListUIState.FailedToLoad
            }
        }
    }

    @Suppress("PrivatePropertyName")
    private val TAG = "DeviceListViewModelImpl"
}
