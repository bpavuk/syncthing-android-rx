package com.nutomic.syncthingandroid.ui.screens.folders

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutomic.syncthingandroid.util.ConfigXml
import com.nutomic.syncthingandroid.util.configkt.ConfigRouterKt
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import syncthingrest.model.folder.FolderID

sealed interface FolderListUIState {
    data object Loading : FolderListUIState
    data object FailedToLoad : FolderListUIState
    data class Success(
        val folders: List<FolderID>
    ) : FolderListUIState
}

interface FolderListViewModel {
    val uiState: StateFlow<FolderListUIState>

    fun updateFolders()
}

class FolderListViewModelImpl(
    private val configRouter: ConfigRouterKt,
) : FolderListViewModel, ViewModel() {
    private val _uiState: MutableStateFlow<FolderListUIState> =
        MutableStateFlow(FolderListUIState.Loading)
    override val uiState: StateFlow<FolderListUIState> = _uiState.asStateFlow()

    override fun updateFolders() {
        viewModelScope.launch {
            try {
                _uiState.value = FolderListUIState.Success(
                    folders = configRouter.folders.getFolders().map { rawFolder ->
                        rawFolder.id
                    }
                )
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

    @Suppress("PrivatePropertyName")
    private val TAG = "FolderListViewModelImpl"
}
