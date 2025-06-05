package syncthingrest.model.folder

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = FolderTypeSerializer::class)
enum class FolderType(val apiName: String) {
    SEND_RECEIVE("sendreceive"),
    SEND_ONLY("sendonly"),
    RECEIVE_ONLY("receiveonly"),
    RECEIVE_ENCRYPTED("receiveencrypted")
}

object FolderTypeSerializer : KSerializer<FolderType> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("FolderType", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: FolderType) {
        encoder.encodeString(value.apiName)
    }

    override fun deserialize(decoder: Decoder): FolderType {
        val apiName = decoder.decodeString()
        return FolderType.entries.first { it.apiName == apiName }
    }
}