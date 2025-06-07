package syncthingrest.model.device.events

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DeviceDisconnectedEventData(
    val error: String,
    val id: String
) : DeviceEventData

@Serializable
@SerialName("DeviceDisconnected")
data class DeviceDisconnectedEvent(
    override val id: Int,
    override val globalID: Int,
    override val type: String,
    override val time: String,
    override val data: DeviceDisconnectedEventData
) : DeviceEvent
