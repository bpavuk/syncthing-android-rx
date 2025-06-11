package syncthingrest.model.device.events

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import syncthingrest.model.device.DeviceID

@Serializable
data class DeviceConnectedEventData(
    val addr: String,
    val id: DeviceID,
    val deviceName: String,
    val clientName: String,
    val clientVersion: String,
    val type: String
) : DeviceEventData

@Serializable
@SerialName("DeviceConnected")
data class DeviceConnectedEvent(
    override val id: Int,
    override val globalID: Int,
    override val type: String,
    override val time: String,
    override val data: DeviceConnectedEventData
) : DeviceEvent
