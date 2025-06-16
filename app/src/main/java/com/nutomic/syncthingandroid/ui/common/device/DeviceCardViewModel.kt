package com.nutomic.syncthingandroid.ui.common.device

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutomic.syncthingandroid.util.ConfigXml
import com.nutomic.syncthingandroid.util.configkt.ConfigRouterKt
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import syncthingrest.model.device.Device
import syncthingrest.model.device.DeviceID
import syncthingrest.model.device.events.DeviceConnectedEvent
import syncthingrest.model.device.events.DeviceConnectedEventData
import syncthingrest.model.device.events.DeviceDisconnectedEvent
import syncthingrest.model.device.events.DeviceDisconnectedEventData
import syncthingrest.model.device.events.DeviceDiscoveredEvent
import syncthingrest.model.device.events.DeviceDiscoveredEventData
import syncthingrest.model.device.events.DevicePausedEvent
import syncthingrest.model.device.events.DevicePausedEventData
import syncthingrest.model.device.events.DeviceResumedEvent
import syncthingrest.model.device.events.DeviceResumedEventData

sealed interface DeviceCardState {
    data class Success(
        val syncState: DeviceCardSyncState,
        val label: String,
        val deviceID: DeviceID
    ) : DeviceCardState
    data object Loading : DeviceCardState
    data object Error : DeviceCardState
}

sealed interface DeviceCardSyncState {
    data object UpToDate : DeviceCardSyncState
    data object Disconnected : DeviceCardSyncState
    data class InProgress(val progress: Float) : DeviceCardSyncState
    data object Error : DeviceCardSyncState
    data object Paused : DeviceCardSyncState
}

interface DeviceCardViewModel {
    val uiState: StateFlow<DeviceCardState>

    fun updateDevice(id: DeviceID)
}

class DeviceCardViewModelImpl(private val configRouter: ConfigRouterKt) : DeviceCardViewModel,
    ViewModel() {
    private val _uiState: MutableStateFlow<DeviceCardState> =
        MutableStateFlow(DeviceCardState.Loading)
    override val uiState: StateFlow<DeviceCardState> = _uiState.asStateFlow()

    private val deviceEventFlow = merge(
        configRouter.devices.pausedEventFlow,
        configRouter.devices.resumedEventFlow,
        configRouter.devices.connectedEventFlow,
        configRouter.devices.disconnectedEventFlow
    )

    override fun updateDevice(id: DeviceID) {
        viewModelScope.launch {
            try {
                _uiState.value = configRouter.devices.getDevice(id).toDeviceState()
                subscribeToDeviceEvents(id)
                Log.d(TAG, "returning from updateDevice")
            } catch (e: ConfigXml.OpenConfigException) {
                Log.e(
                    TAG,
                    "Failed to parse existing config. You will need support from here...",
                    e
                )
                _uiState.value = DeviceCardState.Error
            }
        }
    }

    private suspend fun CoroutineScope.subscribeToDeviceEvents(id: DeviceID) {
        configRouter.subscribeToEvents(this)

        var latestDeviceEventId = 0

        deviceEventFlow.filter { // Getting only events relevant to this particular device
            when (it) {
                is DeviceConnectedEvent -> it.data.id == id
                is DeviceDisconnectedEvent -> it.data.id == id
                is DevicePausedEvent -> it.data.device == id
                is DeviceResumedEvent -> it.data.device == id
                is DeviceDiscoveredEvent -> it.data.device == id
            }
        }.stateIn(this).collect { event ->
            if (event.globalID <= latestDeviceEventId) {
                Log.d(TAG, "globalID is smaller than latest event id, skipping...")
                return@collect
            }
            Log.d(TAG, "New event arrived!!")
            val currentState = _uiState.value
            if (currentState is DeviceCardState.Success) {
                val data = event.data
                when (data) { // Mapping events to sync states
                    is DeviceConnectedEventData -> {
                        Log.d(TAG, "DeviceConnectedEvent")
                        _uiState.emit(
                            currentState.copy(syncState = DeviceCardSyncState.UpToDate)
                        )
                    }
                    is DeviceDisconnectedEventData -> {
                        Log.d(TAG, "DeviceDisconnectedEvent")
                        _uiState.emit(
                            currentState.copy(syncState = DeviceCardSyncState.Disconnected)
                        )
                    }
                    is DevicePausedEventData -> {
                        Log.d(TAG, "DevicePausedEvent")
                        _uiState.emit(
                            currentState.copy(syncState = DeviceCardSyncState.Paused)
                        )
                    }
                    is DeviceResumedEventData -> {
                        Log.d(TAG, "DeviceResumedEvent")
                        _uiState.emit(
                            currentState.copy(syncState = DeviceCardSyncState.UpToDate)
                        )
                    }
                    is DeviceDiscoveredEventData -> {
                        // ignoring!
                    }
                }
            }
            latestDeviceEventId = event.globalID
        }
    }

    companion object {
        private const val TAG = "DeviceCardViewModelImpl"
    }
}

private fun Device?.toDeviceState(): DeviceCardState = if (this == null) {
    DeviceCardState.Error
} else {
    DeviceCardState.Success(
        syncState = if (paused) DeviceCardSyncState.Paused else DeviceCardSyncState.UpToDate,
        label = name,
        deviceID = deviceID,
    )
}
