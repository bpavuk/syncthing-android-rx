package syncthingrest.model.events

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import syncthingrest.model.device.events.DeviceEventData
import syncthingrest.model.folder.events.FolderEventData

/**
 * Custom serializer for [EventData] that dispatches to [DeviceEventData] or [FolderEventData]
 * based on the 'type' field in the JSON payload.
 *
 * This serializer assumes that the 'type' field, which is automatically added by
 * kotlinx.serialization for sealed interfaces like [DeviceEventData] and [FolderEventData],
 * contains a value that allows distinguishing between device-related and folder-related events.
 *
 * For example, if concrete device event types start with "Device" (e.g., "DeviceConnectedEventData")
 * and concrete folder event types start with "Folder" (e.g., "FolderScanProgressEventData").
 */
object EventDataSerializer : JsonContentPolymorphicSerializer<EventData>(EventData::class) {
    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<EventData> {
        val type = element.jsonObject["type"]?.jsonPrimitive?.content
            ?: throw IllegalArgumentException("EventData JSON must contain a 'type' field for polymorphic deserialization.")

        return when {
            type.startsWith("Device") -> DeviceEventData.serializer()
            type.startsWith("Folder") -> FolderEventData.serializer()
            else -> throw IllegalArgumentException("Unknown EventData type: $type")
        }
    }
}
