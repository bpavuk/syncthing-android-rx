package syncthingrest.api

import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.path
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import syncthingrest.RestApiKt
import syncthingrest.logging.Logger
import syncthingrest.model.events.Event
import syncthingrest.model.events.EventData

class EventsApi(
    val restApi: RestApiKt,
    private val logger: Logger = Logger
) {
    // Due to one weird workaround, every time an event is added, you should also add its
    // name here. Use Syncthing event name, not event class name.
    val supportedEvents = listOf(
        "DeviceConnected",
        "DeviceDisconnected",
        "DeviceDiscovered",
        "DevicePaused",
        "DeviceResumed",
        "FolderCompletion",
        "FolderErrors",
        "FolderPaused",
        "FolderResumed",
        "FolderScanProgress",
        "FolderSummary",
        "FolderWatchStateChanged",
    )

    val eventsFlow: Flow<Event<EventData>> = flow {
        var lastSeenId = 0
        while (true) {
            try {
                val eventResponse = restApi.client.get {
                    url {
                        path("rest/events")
                        parameter("timeout", 300) // seconds
                        parameter("events", supportedEvents.joinToString(separator = ","))
                        if (lastSeenId > 0) parameter("since", lastSeenId)
                    }
                }

                val events = eventResponse.body<List<Event<EventData>>>().sortedBy { it.id }
                events.lastOrNull()?.id?.let { lastSeenId = it }
                if (events.isNotEmpty()) events.forEach { emit(it) }
            } catch (e: Exception) {
                logger.e(TAG, "Failed to get an Event: ${e.message}", e)
            } finally {
                delay(timeMillis = 10)
            }
        }
    }

    companion object {
        private const val TAG = "EventsApi"
    }
}
