package syncthingrest.model.folder.events

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import syncthingrest.model.device.DeviceID
import syncthingrest.model.folder.FolderID

@Serializable
data class FolderSummaryData(
    val error: String,
    val errors: Long,
    val globalBytes: Long,
    val globalDeleted: Long,
    val globalDirectories: Long,
    val globalFiles: Long,
    val globalSymlinks: Long,
    val globalTotalItems: Long,
    val ignorePatterns: Boolean,
    val inSyncBytes: Long,
    val inSyncFiles: Long,
    val invalid: String,
    val localBytes: Long,
    val localDeleted: Long,
    val localDirectories: Long,
    val localFiles: Long,
    val localSymlinks: Long,
    val localTotalItems: Long,
    val needBytes: Long,
    val needDeletes: Long,
    val needDirectories: Long,
    val needFiles: Long,
    val needSymlinks: Long,
    val needTotalItems: Long,
    val pullErrors: Long,
    val receiveOnlyChangedBytes: Long,
    val receiveOnlyChangedDeletes: Long,
    val receiveOnlyChangedDirectories: Long,
    val receiveOnlyChangedFiles: Long,
    val receiveOnlyChangedSymlinks: Long,
    val receiveOnlyTotalItems: Long,
    val remoteSequence: Map<DeviceID, Int>?,
    val sequence: Int,
    val state: String,
    val stateChanged: String,
    val version: Int,
    val watchError: String
)

@Serializable
data class FolderSummaryEventData(
    val folder: FolderID,
    val summary: FolderSummaryData
) : FolderEventData

@Serializable
@SerialName("FolderSummary")
data class FolderSummaryEvent(
    override val id: Int,
    override val globalID: Int,
    override val type: String,
    override val time: String,
    override val data: FolderSummaryEventData
) : FolderEvent
