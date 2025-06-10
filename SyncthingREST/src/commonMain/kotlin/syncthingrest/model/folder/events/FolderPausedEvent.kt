package syncthingrest.model.folder.events

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import syncthingrest.model.folder.FolderID

@Serializable
data class FolderPausedEventData(
    val id: FolderID,
    val label: String
) : FolderEventData

@Serializable
@SerialName("FolderPaused")
data class FolderPausedEvent(
    override val id: Int,
    override val globalID: Int,
    override val type: String,
    override val time: String,
    override val data: FolderPausedEventData
) : FolderEvent
