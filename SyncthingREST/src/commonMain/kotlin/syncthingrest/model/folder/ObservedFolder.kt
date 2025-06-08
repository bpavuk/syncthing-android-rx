package syncthingrest.model.folder

import kotlinx.serialization.Serializable

@Serializable
data class ObservedFolder(
    val time: String,
    val id: FolderID,
    val label: String
)
