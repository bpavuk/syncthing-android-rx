package syncthingrest.model.device

import io.matthewnelson.encoding.base32.Base32
import io.matthewnelson.encoding.core.Decoder.Companion.decodeToByteArray
import kotlinx.serialization.Serializable
import syncthingrest.logging.Logger
import syncthingrest.util.Luhn

@Serializable
@JvmInline
value class DeviceID(val value: String) {
    fun checkDeviceID(): Boolean {
        /**
         * See https://github.com/syncthing/syncthing/blob/master/lib/protocol/deviceid.go
         * how syncthing validates device IDs.
         * Old dirty way to check was: return deviceID.matches("^([A-Z0-9]{7}-){7}[A-Z0-9]{7}$");
         */
        var deviceID = value

        // Trim "="
        deviceID = deviceID.replace("=".toRegex(), "")

        // Convert to upper case.
        deviceID = deviceID.uppercase()

        // untypeoify
        deviceID = deviceID.replace("1".toRegex(), "I")
        deviceID = deviceID.replace("0".toRegex(), "O")
        deviceID = deviceID.replace("8".toRegex(), "B")

        // unchunkify
        deviceID = deviceID.replace("-".toRegex(), "")
        deviceID = deviceID.replace(" ".toRegex(), "")

        // Check length.
        return when (deviceID.length) {
            0 -> false
            56 -> {
                // unluhnify(deviceID)
                val bytesIn = deviceID.toByteArray()
                val res = ByteArray(52)
                var i = 0
                while (i < 4) {
                    val p = bytesIn.copyOfRange(i * (13 + 1), (i + 1) * (13 + 1) - 1)
                    p.copyInto(res, i * 13)

                    // Generate check digit.
                    val checkRune: String? = Luhn.generate(p)
//                    Logger.v(TAG, "checkDeviceID: Luhn.generate(${p.toString(Charsets.UTF_8)}) returned ($checkRune)")
                    if (checkRune == null) {
//                        Logger.w(TAG, "checkDeviceID: deviceID=($deviceID): invalid character")
                        return false
                    }
                    if (deviceID.substring((i + 1) * 14 - 1, (i + 1) * 14 - 1 + 1) != checkRune) {
//                        Logger.w(TAG, "checkDeviceID: deviceID=($deviceID): check digit incorrect")
                        return false
                    }
                    i++
                }
                deviceID = String(res)

                val result = runCatching {
                    "$deviceID====".decodeToByteArray(decoder = Base32.Default)
                }
                result.exceptionOrNull()?.let { throwable ->
                    Logger.w(
                        tag = TAG,
                        message = "checkDeviceID: deviceID=($deviceID): invalid character, base32 decode failed"
                    )
                }
                result.isSuccess
            }

            52 -> {
                val result = runCatching {
                    "$deviceID====".decodeToByteArray(Base32.Default)
                }
                result.exceptionOrNull()?.let { throwable ->
                    Logger.w(
                        tag = TAG,
                        message = "checkDeviceID: deviceID=($deviceID): invalid character, base32 decode failed"
                    )
                }
                result.isSuccess
            }
            else -> false
        }

    }

    companion object {
        private const val TAG = "DeviceID"
    }
}