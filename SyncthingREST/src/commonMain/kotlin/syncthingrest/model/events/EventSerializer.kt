package syncthingrest.model.events

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import syncthingrest.model.device.events.DeviceEvent
import syncthingrest.model.folder.events.FolderEvent

object EventSerializer : JsonContentPolymorphicSerializer<Event<*>>(Event::class) {
    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<Event<*>> {
        val type = element.jsonObject["type"]?.jsonPrimitive?.content
            ?: throw IllegalArgumentException("Event JSON must contain a 'type' field for polymorphic deserialization")

        return when {
            type.startsWith("Device") -> DeviceEvent.serializer()
            type.startsWith("Folder") -> FolderEvent.serializer()
            else -> throw IllegalArgumentException("Unknown Event type: $type")
        }
    }
}
