package com.nutomic.syncthingandroid.ui.screens.devices

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.nutomic.syncthingandroid.activities.SyncthingActivity
import com.nutomic.syncthingandroid.model.Device
import com.nutomic.syncthingandroid.util.ConfigRouter
import com.nutomic.syncthingandroid.util.ConfigXml

interface DevicesViewModel {
    val devices: List<Device>

    fun retrieveDevices()
}

class DevicesViewModelImpl(
    application: SyncthingActivity
) : DevicesViewModel, AndroidViewModel(application.applicationContext as Application) {
    private val configRouter = ConfigRouter(application)
    private val restApi = application.api

    override var devices: List<Device> by mutableStateOf(emptyList())
        private set

    override fun retrieveDevices() {
        try {
            configRouter.getDevices(restApi, false)
        } catch (e: ConfigXml.OpenConfigException) {

            Log.e(
                TAG,
                "Failed to parse existing config. You will need support from here...",
                e
            )
        }
    }

    @Suppress("PrivatePropertyName")
    private val TAG = "DevicesViewModelImpl"
}
