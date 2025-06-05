package syncthingrest.model.device

import kotlinx.serialization.Serializable
import syncthingrest.model.folder.IgnoredFolder

@Serializable
data class Device(
    val deviceID: String,
    val name: String = "",
    val addresses: List<String> = listOf("dynamic"),
    val compression: String = "metadata",
    val certName: String = "",
    val introducer: Boolean = false,
    val skipIntroductionRemovals: Boolean = false,
    val introducedBy: String = "",
    val paused: Boolean = false,
    val allowedNetworks: List<String> = emptyList(),
    val autoAcceptFolders: Boolean = false,
    val maxSendKbps: Int = 0,
    val maxRecvKbps: Int = 0,
    val ignoredFolders: List<IgnoredFolder> = emptyList(),
    val maxRequestKiB: Int = 0,
    val untrusted: Boolean = false, // Since v1.12.0
    val remoteGUIPort: Int = 0,
    val numConnections: Int? = 0 // Since v1.25.0
)