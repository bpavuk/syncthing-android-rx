package com.nutomic.syncthingandroid.util

import syncthingrest.model.device.Device
import syncthingrest.model.device.DeviceID
import syncthingrest.model.device.SharedWithDevice
import syncthingrest.model.folder.Folder
import syncthingrest.model.folder.FolderID
import syncthingrest.model.folder.FolderType
import syncthingrest.model.folder.ObservedFolder
import com.nutomic.syncthingandroid.model.Device as JavaDevice
import com.nutomic.syncthingandroid.model.Folder as JavaFolder
import com.nutomic.syncthingandroid.model.SharedWithDevice as JavaSharedWithDevice

fun JavaDevice.toKotlin(): Device =
    Device(
        deviceID = DeviceID(deviceID),
        name = name ?: "",
        addresses = addresses ?: listOf("dynamic"),
        allowedNetworks = emptyList(), // Not available in old Device model
        compression = compression ?: "metadata",
        certName = certName ?: "",
        introducedBy = introducedBy ?: "",
        introducer = introducer,
        paused = paused,
        ignoredFolders = ignoredFolders?.mapNotNull { ignoredJavaFolder ->
            ObservedFolder(
                id = FolderID(ignoredJavaFolder.id ?: return@mapNotNull null),
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

fun JavaFolder.toKotlin(): Folder =
    Folder(
        id = FolderID(id),
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
            SharedWithDevice(
                deviceID = DeviceID(javaSharedWithDevice.deviceID),
                introducedBy = DeviceID(javaSharedWithDevice.introducedBy ?: ""),
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
        invalid = invalid,
        devices = sharedWithDevices?.mapNotNull { it?.toKotlin() } ?: emptyList(),
    )

fun JavaSharedWithDevice.toKotlin() = SharedWithDevice(
    deviceID = DeviceID(deviceID),
    introducedBy = DeviceID(introducedBy ?: ""),
    encryptionPassword = encryptionPassword ?: ""
)

fun Folder.toJava(): JavaFolder =
    JavaFolder().also { java ->
        java.id = id.value
        java.paused = paused
        java.label = label
        java.type = type.apiName
        java.path = path
        java.fsWatcherDelayS = fsWatcherDelayS
        java.autoNormalize = autoNormalize
        java.blockPullOrder = blockPullOrder
        java.caseSensitiveFS = caseSensitiveFS
        java.copyOwnershipFromParent = copyOwnershipFromParent
        java.copiers = copiers
        java.copyRangeMethod = copyRangeMethod
        java.disableFsync = disableFsync
        java.disableSparseFiles = disableSparseFiles
        java.disableTempIndexes = disableTempIndexes
        java.filesystemType = filesystemType
        java.fsWatcherEnabled = fsWatcherEnabled
        java.hashers = hashers
        java.ignoreDelete = ignoreDelete
        java.ignorePerms = ignorePerms
        java.invalid = invalid
        java.markerName = markerName
        java.maxConcurrentWrites = maxConcurrentWrites
        java.maxConflicts = maxConflicts
        java.minDiskFree = minDiskFree?.toJava() ?: JavaFolder.MinDiskFree()
        java.modTimeWindowS = modTimeWindowS
        java.order = order
        java.pullerMaxPendingKiB = pullerMaxPendingKiB
        java.pullerPauseS = pullerPauseS
        java.rescanIntervalS = rescanIntervalS
        java.scanProgressIntervalS = scanProgressIntervalS
        java.sendOwnership = sendOwnership
        java.sendXattrs = sendXattrs
        java.syncOwnership = syncOwnership
        java.caseSensitiveFS = caseSensitiveFS
        java.syncXattrs = syncXattrs
        java.blockPullOrder = blockPullOrder
        java.versioning = versioning?.toJava()
        java.weakHashThresholdPct = weakHashThresholdPct
        devices.forEach {
            java.addDevice(it.toJava())
        }
    }

fun Folder.MinDiskFree.toJava(): JavaFolder.MinDiskFree =
    JavaFolder.MinDiskFree().also { java ->
        java.value = value
        java.unit = unit
    }

fun Folder.Versioning.toJava(): JavaFolder.Versioning =
    JavaFolder.Versioning().also { java ->
        java.type = type
        java.fsPath = fsPath
        java.fsType = fsType
        java.params = params
        java.cleanupIntervalS = cleanupIntervalS
    }

fun SharedWithDevice.toJava(): JavaSharedWithDevice =
    JavaSharedWithDevice().also { java ->
        java.deviceID = deviceID.value
        java.introducedBy = introducedBy.value
        java.encryptionPassword = encryptionPassword
    }