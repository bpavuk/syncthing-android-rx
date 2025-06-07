package syncthingrest.api

import io.ktor.client.call.body
import io.ktor.client.request.get
import syncthingrest.RestApiKt
import syncthingrest.logging.Logger
import syncthingrest.model.device.Device

class DevicesApi(
    private val restApi: RestApiKt,
    private val logger: Logger = Logger,
) {
    companion object {
        private const val TAG = "DevicesApi"
    }

    suspend fun loadDevices(): List<Device> {
        return try {
            restApi.client.get("rest/config/devices").body<List<Device>>()
        } catch (e: Exception) {
            logger.e(TAG, "Failed to load devices: ${e.message}", e)
            emptyList()
        }
    }
}
