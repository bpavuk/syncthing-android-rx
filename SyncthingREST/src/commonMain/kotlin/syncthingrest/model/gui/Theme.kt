package syncthingrest.model.gui

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = ThemeSerializer::class)
enum class Theme(val apiName: String) {
    DEFAULT("default"),
    LIGHT("light"),
    DARK("dark")
}

object ThemeSerializer : KSerializer<Theme> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("Theme", PrimitiveKind.STRING)

    override fun serialize(
        encoder: Encoder,
        value: Theme
    ) {
        encoder.encodeString(value.apiName)
    }

    override fun deserialize(decoder: Decoder): Theme {
        val apiName = decoder.decodeString()
        return Theme.entries.first { theme -> theme.apiName == apiName }
    }
}
