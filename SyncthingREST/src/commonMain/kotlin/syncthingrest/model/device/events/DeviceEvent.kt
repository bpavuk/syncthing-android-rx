package syncthingrest.model.device.events

import kotlinx.serialization.Serializable
import syncthingrest.model.events.Event

@Serializable
sealed interface DeviceEvent : Event<DeviceEventData> {
    override val id: Int
    override val globalID: Int
    override val type: String
    override val time: String
    override val data: DeviceEventData
}
