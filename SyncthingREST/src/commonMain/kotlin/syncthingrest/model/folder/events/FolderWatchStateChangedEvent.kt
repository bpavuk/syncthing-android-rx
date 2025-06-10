package syncthingrest.model.folder.events

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import syncthingrest.model.folder.FolderID

@Serializable
data class FolderWatchStateChangedEventData(
    val folder: FolderID,
    val from: String? = null, // 'from' is optional
    val to: String? = null // 'to' is optional
) : FolderEventData

@Serializable
@SerialName("FolderWatchStateChanged")
data class FolderWatchStateChangedEvent(
    override val id: Int,
    override val globalID: Int,
    override val type: String,
    override val time: String,
    override val data: FolderWatchStateChangedEventData
) : FolderEvent
