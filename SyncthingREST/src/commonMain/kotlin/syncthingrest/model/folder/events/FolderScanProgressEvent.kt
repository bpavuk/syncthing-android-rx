package syncthingrest.model.folder.events

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import syncthingrest.model.folder.FolderID

@Serializable
data class FolderScanProgressEventData(
    val total: Long,
    val rate: Long,
    val current: Long,
    val folder: FolderID
) : FolderEventData

@Serializable
@SerialName("FolderScanProgress")
data class FolderScanProgressEvent(
    override val id: Int,
    override val globalID: Int,
    override val type: String,
    override val time: String,
    override val data: FolderScanProgressEventData
) : FolderEvent
