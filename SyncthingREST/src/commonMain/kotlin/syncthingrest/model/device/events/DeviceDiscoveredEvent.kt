package syncthingrest.model.device.events

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import syncthingrest.model.device.DeviceID

@Serializable
data class DeviceDiscoveredEventData(
    val addrs: List<String>,
    val device: DeviceID
) : DeviceEventData

@Serializable
@SerialName("DeviceDiscovered")
data class DeviceDiscoveredEvent(
    override val id: Int,
    override val globalID: Int,
    override val type: String,
    override val time: String,
    override val data: DeviceDiscoveredEventData
) : DeviceEvent
