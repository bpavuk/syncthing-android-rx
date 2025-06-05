package syncthingrest.model.device

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class ObservedDevice(
    val time: Instant,
    val deviceID: String,
    val name: String,
    val address: String
)
