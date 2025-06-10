package com.nutomic.syncthingandroid.util

import android.content.Context
import android.util.Log
import syncthingrest.RestApiKt
import syncthingrest.model.device.Device
import syncthingrest.model.folder.Folder
import syncthingrest.model.folder.FolderID

class ConfigRouterKt(context: Context, val restApi: RestApiKt) {
    private val configXml = ConfigXml(context)

    suspend fun loadDevices(): List<Device> {
        return restApi.devices.getDevices().fold(
            onSuccess = { it },
            onFailure = { e ->
                Log.e("ConfigRouterKt", "Failed request!", e)
                configXml.loadConfig()
                val javaDevices = configXml.getDevices(true)
                javaDevices.map {
                    it.toKotlin()
                }
            }
        )
    }

    suspend fun getFolders(): List<Folder> {
        return restApi.folders.getFolders().fold(
            onSuccess = { it },
            onFailure = { e ->
                Log.e("ConfigRouterKt", "Failed request!", e)
                configXml.loadConfig()
                val javaFolders = configXml.folders
                javaFolders.map {
                    it.toKotlin()
                }
            }
        )
    }

    suspend fun getFolder(folderId: FolderID): Folder? {
        return restApi.folders.getFolder(folderId).fold(
            onSuccess = { it },
            onFailure = { e ->
                Log.e("ConfigRouterKt", "Failed request!", e)
                configXml.loadConfig()
                configXml.folders?.firstOrNull { it.id == folderId.value }?.toKotlin()
            }
        )
    }

    suspend fun updateFolder(folder: Folder) {
        restApi.folders.updateFolder(folder).fold(
            onSuccess = { it },
            onFailure = { e ->
                Log.e("ConfigRouterKt", "Failed request!", e)
                configXml.loadConfig()
                configXml.updateFolder(folder.toJava())
                configXml.saveChanges()
            }
        )
    }

    suspend fun deleteFolder(id: FolderID) {
        restApi.folders.deleteFolder(id).fold(
            onSuccess = { it },
            onFailure = { e ->
                Log.e("ConfigRouterKt", "Failed request!", e)
                configXml.loadConfig()
                configXml.removeFolder(id.value)
                configXml.saveChanges()
            }
        )
    }
}
