package syncthingrest.model.events

import kotlinx.serialization.Serializable

@Serializable(with = EventDataSerializer::class)
interface EventData
