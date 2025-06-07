package syncthingrest.model.device.events

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DeviceResumedEventData(
    val device: String
) : DeviceEventData

@Serializable
@SerialName("DeviceResumed")
data class DeviceResumedEvent(
    override val id: Int,
    override val globalID: Int,
    override val type: String,
    override val time: String,
    override val data: DeviceResumedEventData
) : DeviceEvent
