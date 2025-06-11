package com.nutomic.syncthingandroid.util.configkt

import android.util.Log
import com.nutomic.syncthingandroid.util.toJava
import com.nutomic.syncthingandroid.util.toKotlin
import syncthingrest.model.folder.Folder
import syncthingrest.model.folder.FolderID

class FolderRouter(private val configRouter: ConfigRouterKt) {
    val folderCompletionEventFlow = configRouter.restApi.folders.folderCompletionEventFlow
    val folderErrorsEventFlow = configRouter.restApi.folders.folderErrorsEventFlow
    val folderPausedEventFlow = configRouter.restApi.folders.folderPausedEventFlow
    val folderResumedEventFlow = configRouter.restApi.folders.folderResumedEventFlow
    val folderScanProgressEventFlow = configRouter.restApi.folders.folderScanProgressEventFlow
    val folderSummaryEventFlow = configRouter.restApi.folders.folderSummaryEventFlow
    val folderWatchStateChangedEventFlow = configRouter.restApi.folders.folderWatchStateChangedEventFlow

    suspend fun getFolders(): List<Folder> {
        return configRouter.restApi.folders.getFolders().fold(
            onSuccess = { it },
            onFailure = { e ->
                Log.e("FolderRouter", "Failed request!", e)
                configRouter.configXml.loadConfig()
                val javaFolders = configRouter.configXml.folders
                javaFolders.map {
                    it.toKotlin()
                }
            }
        )
    }

    suspend fun getFolder(folderId: FolderID): Folder? {
        return configRouter.restApi.folders.getFolder(folderId).fold(
            onSuccess = { it },
            onFailure = { e ->
                Log.e("FolderRouter", "Failed request!", e)
                configRouter.configXml.loadConfig()
                configRouter.configXml.folders?.firstOrNull { it.id == folderId.value }?.toKotlin()
            }
        )
    }

    suspend fun updateFolder(folder: Folder) {
        configRouter.restApi.folders.updateFolder(folder).fold(
            onSuccess = { it },
            onFailure = { e ->
                Log.e("FolderRouter", "Failed request!", e)
                configRouter.configXml.loadConfig()
                configRouter.configXml.updateFolder(folder.toJava())
                configRouter.configXml.saveChanges()
            }
        )
    }

    suspend fun deleteFolder(id: FolderID) {
        configRouter.restApi.folders.deleteFolder(id).fold(
            onSuccess = { it },
            onFailure = { e ->
                Log.e("FolderRouter", "Failed request!", e)
                configRouter.configXml.loadConfig()
                configRouter.configXml.removeFolder(id.value)
                configRouter.configXml.saveChanges()
            }
        )
    }
}
