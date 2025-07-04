package syncthingrest.model

import kotlinx.serialization.Serializable
import syncthingrest.model.device.Device
import syncthingrest.model.device.DeviceID
import syncthingrest.model.folder.Folder
import syncthingrest.model.folder.FolderID

@Serializable
data class Defaults(
    val folder: Folder = Folder(id = FolderID(""), path = ""),
    val device: Device = Device(deviceID = DeviceID("")),
    val ignores: Ignores = Ignores()
)
