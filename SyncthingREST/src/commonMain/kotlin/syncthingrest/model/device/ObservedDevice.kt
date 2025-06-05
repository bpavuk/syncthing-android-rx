package syncthingrest.model.device

import kotlinx.serialization.Serializable

@Serializable
data class ObservedDevice(
    val time: String,
    val deviceID: String,
    val name: String,
    val address: String
)
