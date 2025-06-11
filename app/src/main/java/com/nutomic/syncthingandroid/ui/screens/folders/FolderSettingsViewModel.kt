package com.nutomic.syncthingandroid.ui.screens.folders

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutomic.syncthingandroid.util.configkt.ConfigRouterKt
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import syncthingrest.model.folder.Folder
import syncthingrest.model.folder.FolderID
import syncthingrest.model.folder.FolderType

sealed interface FolderSettingsScreenState {
    data object Loading : FolderSettingsScreenState
    data object FolderNotFound : FolderSettingsScreenState
    data object FailedToLoad : FolderSettingsScreenState
    data class Success(
        val id: FolderID, // cannot be changed
        val path: String, // cannot be changed
        val label: String,
        val type: UIFolderType,
        val paused: Boolean
    ) : FolderSettingsScreenState

    sealed interface UIFolderType {
        data object Send : UIFolderType
        data object SendReceive : UIFolderType
        data object ReceiveOnly : UIFolderType
        // no encryption enablement for existing folders
    }
}

interface FolderSettingsViewModel {
    val uiState: StateFlow<FolderSettingsScreenState>

    fun loadFolder(folderId: FolderID)
    fun updateFolderName(newName: String)
    fun updateFolderType(newType: FolderSettingsScreenState.UIFolderType)
    fun updateFolderPausedState(isPaused: Boolean)
    fun deleteFolder()
    fun saveChanges()
}

class FolderSettingsViewModelImpl(
    private val configRouter: ConfigRouterKt,
) : FolderSettingsViewModel, ViewModel() {
    private var initialFolder: Folder? = null
    private val _uiState =
        MutableStateFlow<FolderSettingsScreenState>(FolderSettingsScreenState.Loading)
    override val uiState: StateFlow<FolderSettingsScreenState> = _uiState.asStateFlow()

    override fun loadFolder(folderId: FolderID) {
        viewModelScope.launch {
            _uiState.value = FolderSettingsScreenState.Loading
            try {
                initialFolder = configRouter.folders.getFolder(folderId)
                if (initialFolder != null) {
                    _uiState.emit(
                        value = FolderSettingsScreenState.Success(
                            id = initialFolder!!.id,
                            path = initialFolder!!.path,
                            label = initialFolder!!.label,
                            type = initialFolder!!.type.toUIFolderType(),
                            paused = initialFolder!!.paused
                        )
                    )
                } else {
                    _uiState.value = FolderSettingsScreenState.FolderNotFound
                    Log.e(TAG, "Folder with ID $folderId not found.")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to load folder with ID $folderId: ${e.message}", e)
                _uiState.value = FolderSettingsScreenState.FailedToLoad
            }
        }
    }

    override fun updateFolderName(newName: String) {
        val currentUiState = _uiState.value
        if (currentUiState is FolderSettingsScreenState.Success) {
            _uiState.value = currentUiState.copy(label = newName)
        }
    }

    override fun updateFolderType(newType: FolderSettingsScreenState.UIFolderType) {
        val currentUiState = _uiState.value
        if (currentUiState is FolderSettingsScreenState.Success) {
            _uiState.value = currentUiState.copy(type = newType)
        }
    }

    override fun updateFolderPausedState(isPaused: Boolean) {
        val currentUiState = _uiState.value
        if (currentUiState is FolderSettingsScreenState.Success) {
            _uiState.value = currentUiState.copy(paused = isPaused)
        }
    }

    override fun deleteFolder() {
        viewModelScope.launch {
            val currentUiState = _uiState.value
            if (currentUiState is FolderSettingsScreenState.Success) {
                configRouter.folders.deleteFolder(currentUiState.id)
            }
        }
    }

    override fun saveChanges() {
        viewModelScope.launch {
            val currentUiState = _uiState.value
            if (currentUiState is FolderSettingsScreenState.Success) {
                sendUpdate(currentUiState)
            }
        }
    }

    private suspend fun sendUpdate(currentUiState: FolderSettingsScreenState.Success) {
        val originalInitialFolder = initialFolder // Store original for potential revert
        val updatedFolder = originalInitialFolder
            ?.applyUiChanges(currentUiState)
            ?: throw IllegalStateException("initialFolder cannot be null when saving changes.")

        try {
            configRouter.folders.updateFolder(updatedFolder)
            initialFolder = updatedFolder // Update initialFolder only on success
        } catch (e: Exception) {
            Log.e(
                TAG,
                "Failed to save changes for folder ${currentUiState.id}: ${e.message}",
                e
            )
            // Revert _uiState to the state derived from the original initialFolder
            originalInitialFolder.let { folder ->
                _uiState.emit(
                    value = FolderSettingsScreenState.Success(
                        id = folder.id,
                        path = folder.path,
                        label = folder.label,
                        type = folder.type.toUIFolderType(),
                        paused = folder.paused
                    )
                )
            }
        }
    }

    private fun FolderType.toUIFolderType(): FolderSettingsScreenState.UIFolderType {
        return when (this) {
            FolderType.SEND_RECEIVE -> FolderSettingsScreenState.UIFolderType.SendReceive
            FolderType.SEND_ONLY -> FolderSettingsScreenState.UIFolderType.Send
            FolderType.RECEIVE_ONLY -> FolderSettingsScreenState.UIFolderType.ReceiveOnly
            FolderType.RECEIVE_ENCRYPTED -> FolderSettingsScreenState.UIFolderType.ReceiveOnly // Existing folders cannot switch to encrypted
        }
    }

    private fun FolderSettingsScreenState.UIFolderType.toFolderType(): FolderType {
        return when (this) {
            FolderSettingsScreenState.UIFolderType.Send -> FolderType.SEND_ONLY
            FolderSettingsScreenState.UIFolderType.SendReceive -> FolderType.SEND_RECEIVE
            FolderSettingsScreenState.UIFolderType.ReceiveOnly -> FolderType.RECEIVE_ONLY
        }
    }

    private fun Folder.applyUiChanges(uiState: FolderSettingsScreenState.Success) = copy(
        label = uiState.label,
        type = uiState.type.toFolderType(),
        paused = uiState.paused
    ) // only values that are allowed to be changed are applied

    companion object {
        private const val TAG = "FolderSettingsViewModel"
    }
}
