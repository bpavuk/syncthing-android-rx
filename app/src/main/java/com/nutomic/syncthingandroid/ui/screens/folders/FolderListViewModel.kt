package com.nutomic.syncthingandroid.ui.screens.folders

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutomic.syncthingandroid.ui.common.folder.FolderCardState
import com.nutomic.syncthingandroid.ui.common.folder.FolderCardSyncState
import com.nutomic.syncthingandroid.util.ConfigXml
import com.nutomic.syncthingandroid.util.configkt.ConfigRouterKt
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import syncthingrest.model.folder.Folder
import syncthingrest.model.folder.events.FolderCompletionEventData
import syncthingrest.model.folder.events.FolderErrorsEventData
import syncthingrest.model.folder.events.FolderPausedEventData
import syncthingrest.model.folder.events.FolderResumedEventData
import syncthingrest.model.folder.events.FolderScanProgressEventData
import syncthingrest.model.folder.events.FolderSummaryEventData
import syncthingrest.model.folder.events.FolderWatchStateChangedEventData

sealed interface FolderListUIState {
    data object Loading : FolderListUIState
    data object FailedToLoad : FolderListUIState
    data class Success(
        val folderCards: List<FolderCardState>
    ) : FolderListUIState
}

interface FolderListViewModel {
    val uiState: StateFlow<FolderListUIState>

    fun updateFolders()
}

class FolderListViewModelImpl(
    private val configRouter: ConfigRouterKt,
) : FolderListViewModel, ViewModel() {
    private val folderEventFlow = merge(
        configRouter.folders.folderPausedEventFlow,
        configRouter.folders.folderResumedEventFlow,
        configRouter.folders.folderCompletionEventFlow
    )

    private val _uiState: MutableStateFlow<FolderListUIState> =
        MutableStateFlow(FolderListUIState.Loading)
    override val uiState: StateFlow<FolderListUIState> = _uiState.asStateFlow()

    override fun updateFolders() {
        viewModelScope.launch {
            try {
                _uiState.value = FolderListUIState.Success(
                    folderCards = configRouter.folders.getFolders().map { rawFolder ->
                        rawFolder.toFolderState()
                    }
                )
                subscribeToFolderEvents()
                Log.d(TAG, "returning from updateFolders")
            } catch (e: ConfigXml.OpenConfigException) {
                Log.e(
                    TAG,
                    "Failed to parse existing config. You will need support from here...",
                    e
                )
                _uiState.value = FolderListUIState.FailedToLoad
            }
        }
    }

    private suspend fun CoroutineScope.subscribeToFolderEvents() {
        var latestFolderEventId = 0
        folderEventFlow.stateIn(this).collect { event ->
            if (event.globalID <= latestFolderEventId) {
                Log.d(TAG, "globalID is smaller than latest event id, skipping...")
                return@collect
            }
            Log.d(TAG, "New event arrived!!")
            val currentState = _uiState.value
            if (currentState is FolderListUIState.Success) {
                val data = event.data
                when (data) {
                    is FolderCompletionEventData -> {
                        Log.d(TAG, "FolderCompletionEvent")
                        _uiState.emit(
                            FolderListUIState.Success(
                                folderCards = currentState.folderCards.map { cardState ->
                                    if (cardState.id == data.folder) {
                                        cardState.copy(
                                            syncState = FolderCardSyncState.InProgress(
                                                progress = data.completion.toFloat() / 100f
                                            )
                                        )
                                    } else {
                                        cardState
                                    }
                                }
                            )
                        )
                    }
                    is FolderErrorsEventData -> TODO()
                    is FolderPausedEventData -> {
                        Log.d(TAG, "FolderPausedEvent")
                        _uiState.emit(
                            FolderListUIState.Success(
                                folderCards = currentState.folderCards.map { cardState ->
                                    if (cardState.id == data.id) {
                                        cardState.copy(syncState = FolderCardSyncState.Paused)
                                    } else {
                                        cardState
                                    }
                                }
                            )
                        )
                    }
                    is FolderResumedEventData -> {
                        Log.d(TAG, "FolderResumedEvent")
                        _uiState.emit(
                            FolderListUIState.Success(
                                folderCards = currentState.folderCards.map { cardState ->
                                    if (cardState.id == data.id) {
                                        cardState.copy(syncState = FolderCardSyncState.UpToDate)
                                    } else {
                                        cardState
                                    }
                                }
                            )
                        )
                    }
                    is FolderScanProgressEventData -> TODO()
                    is FolderSummaryEventData -> TODO()
                    is FolderWatchStateChangedEventData -> TODO()
                }
            }
            latestFolderEventId = event.globalID
        }
    }

    @Suppress("PrivatePropertyName")
    private val TAG = "FolderListViewModelImpl"
}

// TODO: make it more robust once events are implemented
private fun Folder.toFolderState(): FolderCardState = FolderCardState(
    syncState = if (paused) FolderCardSyncState.Paused else FolderCardSyncState.UpToDate,
    id = id,
    label = label,
    path = path,
)
