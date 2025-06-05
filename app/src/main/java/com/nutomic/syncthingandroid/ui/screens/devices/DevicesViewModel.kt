package com.nutomic.syncthingandroid.ui.screens.devices

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.nutomic.syncthingandroid.util.ConfigRouterKt
import com.nutomic.syncthingandroid.util.ConfigXml
import kotlinx.coroutines.launch
import syncthingrest.model.device.Device

interface DevicesViewModel {
    val devices: List<Device>

    fun updateDevices()
}

class DevicesViewModelImpl(
    application: Application,
    private val configRouter: ConfigRouterKt,
) : DevicesViewModel, AndroidViewModel(application) {

    override var devices: List<Device> by mutableStateOf(emptyList())
        private set

    override fun updateDevices() {
        viewModelScope.launch {
            try {
                devices = configRouter.loadDevices()
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
    private val TAG = "DevicesViewModelImpl"
}
