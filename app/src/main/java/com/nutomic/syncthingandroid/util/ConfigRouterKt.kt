package com.nutomic.syncthingandroid.util

import android.content.Context
import android.util.Log
import syncthingrest.RestApiKt
import syncthingrest.model.device.Device
import syncthingrest.model.folder.Folder
import syncthingrest.model.folder.IgnoredFolder
import com.nutomic.syncthingandroid.model.Device as JavaDevice
import com.nutomic.syncthingandroid.model.Folder as JavaFolder

class ConfigRouterKt(context: Context, val restApi: RestApiKt) {
    private val configXml = ConfigXml(context)

    suspend fun loadDevices(): List<Device> {
        return try {
            restApi.loadDevices()
        } catch (e: Exception) {
            Log.e("ConfigRouterKt", "Failed request!", e)
            configXml.loadConfig()
            val javaDevices = configXml.getDevices(true)
            javaDevices.map {
                it.toKotlin()
            }
        }
    }

    suspend fun getFolders(): List<Folder> {
        return try {
            restApi.getFolders()
        } catch (e: Exception) {
            Log.e("ConfigRouterKt", "Failed request!", e)
            configXml.loadConfig()
            val javaFolders = configXml.folders
            javaFolders.map {
                it.toKotlin()
            }
        }
    }

    private fun JavaDevice.toKotlin(): Device =
        Device(
            deviceID = deviceID,
            name = name ?: "",
            addresses = addresses ?: listOf("dynamic"),
            allowedNetworks = emptyList(), // Not available in old Device model
            compression = compression ?: "metadata",
            certName = certName ?: "",
            introducedBy = introducedBy ?: "",
            introducer = introducer,
            paused = paused,
            ignoredFolders = ignoredFolders?.mapNotNull { ignoredJavaFolder ->
                IgnoredFolder(
                    id = ignoredJavaFolder.id ?: return@mapNotNull null,
                    label = ignoredJavaFolder.label ?: return@mapNotNull null,
                    time = ignoredJavaFolder.time ?: return@mapNotNull null
                )
            } ?: emptyList(),
            autoAcceptFolders = autoAcceptFolders,
            maxRecvKbps = maxRecvKbps ?: 0,
            maxSendKbps = maxSendKbps ?: 0,
            untrusted = untrusted,
            numConnections = 0 // Not available in old Device model
        )

    private fun JavaFolder.toKotlin(): Folder =
        Folder(

        )
}