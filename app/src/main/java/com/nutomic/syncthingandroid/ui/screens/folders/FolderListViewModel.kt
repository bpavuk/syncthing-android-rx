package com.nutomic.syncthingandroid.ui.screens.folders

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.nutomic.syncthingandroid.ui.common.folder.FolderCardDataView
import com.nutomic.syncthingandroid.ui.common.folder.FolderCardState
import com.nutomic.syncthingandroid.ui.common.folder.FolderCardSyncState
import com.nutomic.syncthingandroid.util.ConfigRouterKt
import com.nutomic.syncthingandroid.util.ConfigXml
import syncthingrest.model.folder.Folder

interface FolderListViewModel {
    val folders: List<FolderCardState>

    suspend fun retrieveFolders()
}

class FolderListViewModelImpl(
    private val configRouter: ConfigRouterKt,
) : FolderListViewModel, ViewModel() {

    override var folders: List<FolderCardState> by mutableStateOf(emptyList())
        private set

    override suspend fun retrieveFolders() {
        try {
            folders = configRouter.getFolders().map { rawFolder ->
                rawFolder.toFolderState()
            }
        } catch (e: ConfigXml.OpenConfigException) {
            Log.e(
                TAG,
                "Failed to parse existing config. You will need support from here...",
                e
            )
            folders = emptyList()
        }
    }

    @Suppress("PrivatePropertyName")
    private val TAG = "FolderListViewModelImpl"
}

// TODO: make it more robust once events are implemented
private fun Folder.toFolderState(): FolderCardState = FolderCardState(
    state = if (paused) FolderCardSyncState.Paused else FolderCardSyncState.UpToDate,
    view = FolderCardDataView(
        label = label,
        path = path,
    )
)
