package syncthingrest.model.device.events

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DeviceDiscoveredEventData(
    val addrs: List<String>,
    val device: String
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
