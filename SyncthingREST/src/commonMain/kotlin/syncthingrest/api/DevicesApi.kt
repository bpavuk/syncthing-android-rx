package syncthingrest.api

import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.flow.mapNotNull
import syncthingrest.RestApiKt
import syncthingrest.logging.Logger
import syncthingrest.model.device.Device
import syncthingrest.model.device.events.DeviceConnectedEvent
import syncthingrest.model.device.events.DeviceDisconnectedEvent
import syncthingrest.model.device.events.DeviceDiscoveredEvent
import syncthingrest.model.device.events.DeviceResumedEvent

class DevicesApi(
    private val restApi: RestApiKt,
    eventsApi: EventsApi,
    private val logger: Logger = Logger,
) {
    val deviceConnectedEventFlow = eventsApi.eventsFlow.mapNotNull { events ->
        events.filterIsInstance<DeviceConnectedEvent>().ifEmpty { null }
    }
    val deviceDisconnectedEventFlow = eventsApi.eventsFlow.mapNotNull { events ->
        events.filterIsInstance<DeviceDisconnectedEvent>().ifEmpty { null }
    }
    val deviceDiscoveredEventFlow = eventsApi.eventsFlow.mapNotNull { events ->
        events.filterIsInstance<DeviceDiscoveredEvent>().ifEmpty { null }
    }
    val deviceResumedEventFlow = eventsApi.eventsFlow.mapNotNull { events ->
        events.filterIsInstance<DeviceResumedEvent>().ifEmpty { null }
    }

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
