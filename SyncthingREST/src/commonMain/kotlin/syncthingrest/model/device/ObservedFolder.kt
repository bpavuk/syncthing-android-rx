package syncthingrest.model.device

import kotlinx.serialization.Serializable

@Serializable
data class ObservedFolder(
    val time: String,
    val id: String,
    val label: String
)
