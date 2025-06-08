package syncthingrest.model.device

import kotlinx.serialization.Serializable

@Serializable
data class SharedWithDevice(
    var deviceID: DeviceID,
    var introducedBy: DeviceID = DeviceID(""),

    // Since v1.12.0
    // See https://github.com/syncthing/syncthing/pull/7055
    var encryptionPassword: String = "",
) {
    /**
     * Returns the device name, or the first characters of the ID if the name is empty.
     */
    val displayName: String
        get() = (if (deviceID.value.isEmpty()) "" else deviceID.value.substring(0, 7))
}