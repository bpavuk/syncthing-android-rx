package syncthingrest.model.folder.events

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import syncthingrest.model.folder.FolderID

@Serializable
data class ErrorEntry(
    val error: String,
    val path: String
)

@Serializable
data class FolderErrorsEventData(
    val errors: List<ErrorEntry>,
    val folder: FolderID
) : FolderEventData

@Serializable
@SerialName("FolderErrors")
data class FolderErrorsEvent(
    override val id: Int,
    override val globalID: Int,
    override val type: String,
    override val time: String,
    override val data: FolderErrorsEventData
) : FolderEvent
