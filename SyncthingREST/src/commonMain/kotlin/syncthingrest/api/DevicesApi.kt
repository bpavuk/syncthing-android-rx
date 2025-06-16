package syncthingrest.api

import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.filterIsInstance
import syncthingrest.RestApiKt
import syncthingrest.logging.Logger
import syncthingrest.model.device.Device
import syncthingrest.model.device.DeviceID
import syncthingrest.model.device.events.DeviceConnectedEvent
import syncthingrest.model.device.events.DeviceDisconnectedEvent
import syncthingrest.model.device.events.DeviceDiscoveredEvent
import syncthingrest.model.device.events.DevicePausedEvent
import syncthingrest.model.device.events.DeviceResumedEvent

class DevicesApi(
    private val restApi: RestApiKt,
    eventsApi: EventsApi,
    private val logger: Logger = Logger,
) {
    val deviceConnectedEventFlow =
        eventsApi.eventsSharedFlow.filterIsInstance<DeviceConnectedEvent>()
    val deviceDisconnectedEventFlow =
        eventsApi.eventsSharedFlow.filterIsInstance<DeviceDisconnectedEvent>()
    val deviceDiscoveredEventFlow =
        eventsApi.eventsSharedFlow.filterIsInstance<DeviceDiscoveredEvent>()
    val deviceResumedEventFlow = eventsApi.eventsSharedFlow.filterIsInstance<DeviceResumedEvent>()
    val devicePausedEventFlow = eventsApi.eventsSharedFlow.filterIsInstance<DevicePausedEvent>()

    companion object {
        private const val TAG = "DevicesApi"
    }

    suspend fun getDevices(): Result<List<Device>> {
        return try {
            Result.success(restApi.client.get("rest/config/devices").body<List<Device>>())
        } catch (e: Exception) {
            logger.e(TAG, "Failed to load devices: ${e.message}", e as? Exception?)
            Result.failure(e)
        }
    }

    suspend fun getDevice(id: DeviceID): Result<Device?> {
        return try {
            val response = restApi.client.get("rest/config/devices/${id.value}")
            if (response.status == HttpStatusCode.NotFound) {
                Result.success(null)
            } else {
                Result.success(response.body<Device>())
            }
        } catch (e: Exception) {
            logger.e(TAG, "Failed to get device id ${id.value}: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun updateDevices(devices: List<Device>): Result<Unit> {
        return try {
            restApi.client.put("rest/config/devices") {
                setBody(devices)
            }.body<String>()
            Result.success(Unit)
        } catch (e: Exception) {
            logger.e(TAG, "Failed to put devices: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun updateDevices(vararg devices: Device) = updateDevices(devices = devices.toList())

    suspend fun updateDevice(device: Device) = updateDevices(devices = listOf(device))

    suspend fun addDevices(devices: List<Device>): Result<Unit> {
        return try {
            val allDevices = getDevices().getOrThrow()
            val pureDevices = devices.filterNot { device ->
                device in allDevices
            }
            Result.success(updateDevices(pureDevices).getOrThrow())
        } catch (e: Exception) {
            logger.e(TAG, "Failed to add devices: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun addDevices(vararg devices: Device) = addDevices(devices = devices.toList())

    suspend fun addDevice(device: Device) = addDevices(devices = listOf(device))

    suspend fun deleteDevice(id: DeviceID): Result<Unit?> {
        return try {
            val response = restApi.client.delete("rest/config/devices/${id.value}")
            if (response.status == HttpStatusCode.NotFound) {
                Result.success(null)
            } else {
                Result.success(Unit)
            }
        } catch (e: Exception) {
            logger.e(TAG, "Failed to delete device: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun pauseDevice(id: DeviceID): Result<Unit> {
        return try {
            restApi.client.patch("rest/config/devices/${id.value}") {
                setBody(mapOf("paused" to true))
            }.body<String>()
            Result.success(Unit)
        } catch (e: Exception) {
            logger.e(TAG, "Failed to pause device ${id.value}: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun pauseDevice(device: Device): Result<Unit> = pauseDevice(device.deviceID)

    suspend fun resumeDevice(id: DeviceID): Result<Unit> {
        return try {
            restApi.client.patch("rest/config/devices/${id.value}") {
                setBody(mapOf("paused" to false))
            }.body<String>()
            Result.success(Unit)
        } catch (e: Exception) {
            logger.e(TAG, "Failed to resume device ${id.value}: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun resumeDevice(device: Device): Result<Unit> = resumeDevice(device.deviceID)
}
