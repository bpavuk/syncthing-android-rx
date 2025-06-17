package com.nutomic.syncthingandroid.ui.screens.folders

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
import kotlin.random.Random

data class FolderCreationScreenState(
    val id: FolderID = generateFolderID(),
    val path: String = "",
    val label: String = "",
    val type: UIFolderType = UIFolderType.SendReceive,
    val errors: Set<FolderCreationErrors> = emptySet()
) {
    sealed interface UIFolderType {
        data object Send : UIFolderType
        data object SendReceive : UIFolderType
        data object ReceiveOnly : UIFolderType
        data object ReceiveEncrypted : UIFolderType
    }
}

sealed interface FolderCreationErrors {
    data object EmptyID : FolderCreationErrors
    data object EmptyLabel : FolderCreationErrors
    data object EmptyDirectory : FolderCreationErrors
    data object APIFailure : FolderCreationErrors
}

interface FolderCreationViewModel {
    val uiState: StateFlow<FolderCreationScreenState>

    fun updateFolderName(newName: String)
    fun updateFolderPath(newPath: String)
    fun updateFolderType(newType: FolderCreationScreenState.UIFolderType)
    fun updateFolderId(newId: String)
    fun updateFolderId(newId: FolderID)
    fun createFolder()
}

class FolderCreationViewModelImpl(
    private val configRouter: ConfigRouterKt,
) : FolderCreationViewModel, ViewModel() {
    private val _uiState =
        MutableStateFlow(FolderCreationScreenState())
    override val uiState: StateFlow<FolderCreationScreenState> = _uiState.asStateFlow()

    override fun updateFolderName(newName: String) {
        val currentUiState = _uiState.value
        _uiState.value = currentUiState.copy(label = newName)
    }

    override fun updateFolderPath(newPath: String) {
        val currentUiState = _uiState.value
        _uiState.value = currentUiState.copy(path = newPath)
    }

    override fun updateFolderType(newType: FolderCreationScreenState.UIFolderType) {
        val currentUiState = _uiState.value
        _uiState.value = currentUiState.copy(type = newType)
    }

    override fun updateFolderId(newId: FolderID) {
        val currentUiState = _uiState.value
        _uiState.value = currentUiState.copy(id = newId)
    }

    override fun updateFolderId(newId: String) {
        updateFolderId(FolderID(newId))
    }

    override fun createFolder() {
        viewModelScope.launch {
            checkInputs()
            val currentUiState = _uiState.value
            if (currentUiState.errors.isNotEmpty()) {
                return@launch
            }
            val newFolder = Folder(
                id = currentUiState.id,
                label = currentUiState.label,
                path = currentUiState.path,
                type = currentUiState.type.toFolderType(),
                // Set other default values as needed for creation
                minDiskFree = Folder.MinDiskFree(),
                versioning = Folder.Versioning(
                    type = "trashcan",
                    params = mutableMapOf("cleanoutDays" to "14")
                ),
                order = "random",
                blockPullOrder = "standard",
                copyRangeMethod = "standard",
            )
            configRouter.folders.createFolder(newFolder)
        }
    }

    private fun checkInputs() {
        val currentState = _uiState.value
        val errorSet: MutableSet<FolderCreationErrors> = mutableSetOf()
        if (currentState.id.value.isEmpty()) errorSet.add(FolderCreationErrors.EmptyID)
        if (currentState.label.isEmpty()) errorSet.add(FolderCreationErrors.EmptyLabel)
        if (currentState.path.isEmpty()) errorSet.add(FolderCreationErrors.EmptyDirectory)
        _uiState.value = _uiState.value.copy(errors = errorSet)
    }

    private fun FolderCreationScreenState.UIFolderType.toFolderType(): FolderType {
        return when (this) {
            FolderCreationScreenState.UIFolderType.Send -> FolderType.SEND_ONLY
            FolderCreationScreenState.UIFolderType.SendReceive -> FolderType.SEND_RECEIVE
            FolderCreationScreenState.UIFolderType.ReceiveOnly -> FolderType.RECEIVE_ONLY
            FolderCreationScreenState.UIFolderType.ReceiveEncrypted -> FolderType.RECEIVE_ENCRYPTED
        }
    }

    companion object {
        private const val TAG = "FolderCreationViewModel"
    }
}

private fun generateFolderID(): FolderID {
    val chars = "abcdefghijklmnopqrstuvwxyz0123456789".toCharArray()
    val sb = StringBuilder()
    for (i in 0 until 10) {
        if (i == 5) {
            sb.append("-")
        }
        val c = chars[Random.nextInt(chars.size)]
        sb.append(c)
    }
    return FolderID(sb.toString())
}
