package syncthingrest.model.device

import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class DeviceID(val value: String)