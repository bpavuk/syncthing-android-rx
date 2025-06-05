package syncthingrest.model

import kotlinx.serialization.Serializable
import syncthingrest.model.device.Device

@Serializable
private data class SystemConfig(
    val devices: List<Device>
    // Add other fields from the config if needed, e.g., folders, gui, etc.
)
