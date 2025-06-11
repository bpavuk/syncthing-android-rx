package com.nutomic.syncthingandroid.ui.common.folder

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutomic.syncthingandroid.ui.common.folder.FolderCardSyncState.InProgress
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
import syncthingrest.model.folder.Folder
import syncthingrest.model.folder.FolderID
import syncthingrest.model.folder.events.FolderCompletionEvent
import syncthingrest.model.folder.events.FolderCompletionEventData
import syncthingrest.model.folder.events.FolderErrorsEvent
import syncthingrest.model.folder.events.FolderErrorsEventData
import syncthingrest.model.folder.events.FolderPausedEvent
import syncthingrest.model.folder.events.FolderPausedEventData
import syncthingrest.model.folder.events.FolderResumedEvent
import syncthingrest.model.folder.events.FolderResumedEventData
import syncthingrest.model.folder.events.FolderScanProgressEvent
import syncthingrest.model.folder.events.FolderScanProgressEventData
import syncthingrest.model.folder.events.FolderSummaryEvent
import syncthingrest.model.folder.events.FolderSummaryEventData
import syncthingrest.model.folder.events.FolderWatchStateChangedEvent
import syncthingrest.model.folder.events.FolderWatchStateChangedEventData

interface FolderCardViewModel {
    val uiState: StateFlow<FolderCardState>

    fun updateFolder(id: FolderID)
}

class FolderCardViewModelImpl(private val configRouter: ConfigRouterKt) : FolderCardViewModel,
    ViewModel() {
    private val _uiState: MutableStateFlow<FolderCardState> =
        MutableStateFlow(FolderCardState.Loading)
    override val uiState: StateFlow<FolderCardState> = _uiState.asStateFlow()

    private val folderEventFlow = merge(
        configRouter.folders.folderPausedEventFlow,
        configRouter.folders.folderResumedEventFlow,
        configRouter.folders.folderSummaryEventFlow
    )

    override fun updateFolder(id: FolderID) {
        viewModelScope.launch {
            try {
                _uiState.value = configRouter.folders.getFolder(id).toFolderState()
                subscribeToFolderEvents(id)
                Log.d(TAG, "returning from updateFolders")
            } catch (e: ConfigXml.OpenConfigException) {
                Log.e(
                    TAG,
                    "Failed to parse existing config. You will need support from here...",
                    e
                )
                _uiState.value = FolderCardState.Error
            }
        }
    }

    private suspend fun CoroutineScope.subscribeToFolderEvents(id: FolderID) {
        var latestFolderEventId = 0
        folderEventFlow.filter {
            when (it) {
                is FolderCompletionEvent -> it.data.folder == id
                is FolderErrorsEvent -> it.data.folder == id
                is FolderPausedEvent -> it.data.id == id
                is FolderResumedEvent -> it.data.id == id
                is FolderScanProgressEvent -> it.data.folder == id
                is FolderSummaryEvent -> it.data.folder == id
                is FolderWatchStateChangedEvent -> it.data.folder == id
            }
        }.stateIn(this).collect { event ->
            if (event.globalID <= latestFolderEventId) {
                Log.d(TAG, "globalID is smaller than latest event id, skipping...")
                return@collect
            }
            Log.d(TAG, "New event arrived!!")
            val currentState = _uiState.value
            if (currentState is FolderCardState.Success) {
                val data = event.data
                when (data) {
                    is FolderSummaryEventData -> {
                        Log.d(TAG, "FolderCompletionEvent, data: $data")
                        _uiState.emit(
                            currentState.copy(
                                syncState = if (data.summary.needBytes == 0L) {
                                    FolderCardSyncState.UpToDate
                                } else {
                                    InProgress(
                                        progress = (data.summary.globalBytes - data.summary.needBytes).toFloat() / data.summary.globalBytes
                                    )
                                }
                            )
                        )
                    }

                    is FolderErrorsEventData -> TODO()
                    is FolderPausedEventData -> {
                        Log.d(TAG, "FolderPausedEvent")
                        _uiState.emit(
                            currentState.copy(syncState = FolderCardSyncState.Paused)
                        )
                    }

                    is FolderResumedEventData -> {
                        Log.d(TAG, "FolderResumedEvent")
                        _uiState.emit(
                            currentState.copy(syncState = FolderCardSyncState.UpToDate)
                        )
                    }

                    is FolderScanProgressEventData -> TODO()
                    is FolderWatchStateChangedEventData -> TODO()
                    is FolderCompletionEventData -> TODO()
                }
            }
            latestFolderEventId = event.globalID
        }
    }

    companion object {
        private const val TAG = "FolderCardViewModelImpl"
    }
}

private fun Folder?.toFolderState(): FolderCardState = if (this == null) {
    FolderCardState.Error
} else {
    FolderCardState.Success(
        syncState = if (paused) FolderCardSyncState.Paused else FolderCardSyncState.UpToDate,
        id = id,
        label = label,
        path = path,
    )
}
