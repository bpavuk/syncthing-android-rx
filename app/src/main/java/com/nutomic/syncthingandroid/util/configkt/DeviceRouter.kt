package com.nutomic.syncthingandroid.util.configkt

import android.util.Log
import com.nutomic.syncthingandroid.util.toKotlin
import syncthingrest.model.device.Device

class DeviceRouter(private val configRouter: ConfigRouterKt) {
    val pausedEventFlow = configRouter.restApi.devices.devicePausedEventFlow
    val resumedEventFlow = configRouter.restApi.devices.deviceResumedEventFlow
    val disconnectedEventFlow = configRouter.restApi.devices.deviceDisconnectedEventFlow
    val connectedEventFlow = configRouter.restApi.devices.deviceConnectedEventFlow

    suspend fun loadDevices(): List<Device> {
        return configRouter.restApi.devices.getDevices().fold(
            onSuccess = { it },
            onFailure = { e ->
                Log.e("DeviceRouter", "Failed request!", e)
                configRouter.configXml.loadConfig()
                val javaDevices = configRouter.configXml.getDevices(true)
                javaDevices.map {
                    it.toKotlin()
                }
            }
        )
    }
}
