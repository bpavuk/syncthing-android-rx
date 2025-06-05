package syncthingrest.model.device

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class ObservedFolder(
    val time: Instant,
    val id: String,
    val label: String
)
