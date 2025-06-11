package syncthingrest.model.device.events

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import syncthingrest.model.device.DeviceID

@Serializable
data class DevicePausedEventData(
    val device: DeviceID
) : DeviceEventData

@Serializable
@SerialName("DevicePaused")
data class DevicePausedEvent(
    override val id: Int,
    override val globalID: Int,
    override val type: String,
    override val time: String,
    override val data: DevicePausedEventData
) : DeviceEvent
