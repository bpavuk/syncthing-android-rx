package syncthingrest.model.folder.events

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = RemoteStateSerializer::class)
enum class RemoteState(val apiName: String) {
    PAUSED("paused"),
    NOT_SHARING("notSharing"),
    VALID("valid"),
    UNKNOWN("unknown")
}

object RemoteStateSerializer : KSerializer<RemoteState> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("RemoteState", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: RemoteState) {
        encoder.encodeString(value.apiName)
    }

    override fun deserialize(decoder: Decoder): RemoteState {
        val apiName = decoder.decodeString()
        return RemoteState.entries.first { it.apiName == apiName }
    }
}
