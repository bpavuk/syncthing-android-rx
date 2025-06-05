package syncthingrest.model

import kotlinx.serialization.Serializable

@Serializable
data class Defaults(
    val folder: Folder = Folder(id = "", path = ""),
    val device: Device = Device(deviceID = ""),
    val ignores: Ignores = Ignores()
)
