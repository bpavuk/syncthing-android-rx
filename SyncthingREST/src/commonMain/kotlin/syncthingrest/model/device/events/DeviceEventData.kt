package syncthingrest.model.device.events

import kotlinx.serialization.Serializable
import syncthingrest.model.events.EventData

@Serializable
sealed interface DeviceEventData : EventData
