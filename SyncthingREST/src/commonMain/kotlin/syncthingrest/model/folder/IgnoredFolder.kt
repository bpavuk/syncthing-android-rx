package syncthingrest.model.folder

import kotlinx.serialization.Serializable

@Serializable
data class IgnoredFolder(
    val id: String = "",
    val label: String = "",
    val time: String = ""
)