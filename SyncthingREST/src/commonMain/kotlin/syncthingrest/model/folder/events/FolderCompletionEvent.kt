package syncthingrest.model.folder.events

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FolderCompletionEventData(
    val completion: Int,
    val device: String,
    val folder: String,
    val globalBytes: Long,
    val globalItems: Long,
    val needBytes: Long,
    val needDeletes: Long,
    val needItems: Long,
    val remoteState: RemoteState,
    val sequence: Int
) : FolderEventData

@Serializable
@SerialName("FolderCompletion")
data class FolderCompletionEvent(
    override val id: Int,
    override val globalID: Int,
    override val type: String,
    override val time: String,
    override val data: FolderCompletionEventData
) : FolderEvent
