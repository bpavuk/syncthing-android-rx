package syncthingrest.model.events

import kotlinx.serialization.Serializable

@Serializable(with = EventSerializer::class)
interface Event<T : EventData> {
    val id: Int
    val globalID: Int
    val type: String
    val time: String
    val data: T
}
