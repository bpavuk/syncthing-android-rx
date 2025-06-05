package syncthingrest.model

import kotlinx.serialization.Serializable
import syncthingrest.model.device.Device
import syncthingrest.model.device.ObservedDevice
import syncthingrest.model.folder.Folder
import syncthingrest.model.gui.Gui

@Serializable
data class Config(
    var version: Int,
    var devices: List<Device> = listOf(),
    var folders: List<Folder> = listOf(),
    var gui: Gui,
    var options: Options,
    var defaults: Defaults,
    var remoteIgnoredDevices: List<ObservedDevice> = listOf()
)
