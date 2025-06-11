package syncthingrest.model.device.events

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import syncthingrest.model.device.DeviceID

@Serializable
data class DeviceDisconnectedEventData(
    val error: String,
    val id: DeviceID
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
