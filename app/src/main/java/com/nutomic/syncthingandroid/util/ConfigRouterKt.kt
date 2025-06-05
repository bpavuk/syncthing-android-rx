package com.nutomic.syncthingandroid.util

import android.content.Context
import android.util.Log
import syncthingrest.RestApiKt
import syncthingrest.model.device.Device
import syncthingrest.model.folder.Folder
import syncthingrest.model.folder.FolderType
import syncthingrest.model.folder.IgnoredFolder
import com.nutomic.syncthingandroid.model.Device as JavaDevice
import com.nutomic.syncthingandroid.model.Folder as JavaFolder
import syncthingrest.model.device.SharedWithDevice as KotlinSharedWithDevice

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
            id = id,
            label = label ?: "",
            filesystemType = filesystemType ?: "basic",
            path = path,
            type = when (type) {
                "sendreceive" -> FolderType.SEND_RECEIVE
                "sendonly" -> FolderType.SEND_ONLY
                "receiveonly" -> FolderType.RECEIVE_ONLY
                "receiveencrypted" -> FolderType.RECEIVE_ENCRYPTED
                else -> FolderType.SEND_RECEIVE // Default from Kotlin Folder
            },
            fsWatcherEnabled = fsWatcherEnabled,
            fsWatcherDelayS = fsWatcherDelayS,
            sharedWithDevices = sharedWithDevices.map { javaSharedWithDevice ->
                KotlinSharedWithDevice(
                    deviceID = javaSharedWithDevice.deviceID,
                    introducedBy = javaSharedWithDevice.introducedBy ?: "",
                    encryptionPassword = javaSharedWithDevice.encryptionPassword ?: ""
                )
            }.toMutableList(),
            rescanIntervalS = rescanIntervalS,
            ignorePerms = ignorePerms,
            autoNormalize = autoNormalize,
            minDiskFree = minDiskFree?.let { javaMinDiskFree ->
                Folder.MinDiskFree(
                    value = javaMinDiskFree.value,
                    unit = javaMinDiskFree.unit ?: "%"
                )
            },
            versioning = versioning?.let { javaVersioning ->
                Folder.Versioning(
                    type = javaVersioning.type,
                    cleanupIntervalS = javaVersioning.cleanupIntervalS,
                    params = javaVersioning.params?.mapValues { it.value }?.toMutableMap() ?: mutableMapOf(),
                    fsPath = javaVersioning.fsPath,
                    fsType = javaVersioning.fsType ?: "basic"
                )
            },
            copiers = copiers,
            pullerMaxPendingKiB = pullerMaxPendingKiB,
            hashers = hashers,
            order = order ?: "random",
            ignoreDelete = ignoreDelete,
            scanProgressIntervalS = scanProgressIntervalS,
            pullerPauseS = pullerPauseS,
            maxConflicts = maxConflicts,
            disableSparseFiles = disableSparseFiles,
            disableTempIndexes = disableTempIndexes,
            paused = paused,
            weakHashThresholdPct = weakHashThresholdPct,
            copyOwnershipFromParent = copyOwnershipFromParent ?: false,
            modTimeWindowS = modTimeWindowS,
            blockPullOrder = blockPullOrder ?: "standard",
            disableFsync = disableFsync ?: false,
            maxConcurrentWrites = maxConcurrentWrites,
            copyRangeMethod = copyRangeMethod ?: "standard",
            caseSensitiveFS = caseSensitiveFS ?: false,
            syncOwnership = syncOwnership ?: false,
            sendOwnership = sendOwnership ?: false,
            syncXattrs = syncXattrs ?: false,
            sendXattrs = sendXattrs ?: false,
            invalid = invalid
        )
}
