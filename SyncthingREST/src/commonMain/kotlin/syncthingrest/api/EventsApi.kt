package syncthingrest.api

import io.ktor.client.call.body
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.path
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import syncthingrest.RestApiKt
import syncthingrest.logging.Logger
import syncthingrest.model.events.Event
import syncthingrest.model.events.EventData
import java.net.ConnectException

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

    private val _eventsSharedFlow: MutableSharedFlow<Event<EventData>> = MutableSharedFlow(
        replay = 10,
        extraBufferCapacity = 100,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val eventsSharedFlow = _eventsSharedFlow.asSharedFlow()

    private var sharingJob: Job? = null

    fun startEvents(coroutineScope: CoroutineScope) {
        Logger.d(TAG + "SharedFlow", "startEvents invocation")

        if (sharingJob != null && sharingJob?.isActive == true) {
            Logger.d(TAG + "SharedFlow", "duplicate invocation, returning")
            return
        }

        Logger.d(TAG + "SharedFlow", "first invocation. starting collection...")

        sharingJob = coroutineScope.launch {
            var lastSeenId = 0
            while (true) {
                try {
                    val eventResponse = restApi.client.get {
                        url {
                            path("rest/events")
                            parameter("timeout", 3) // seconds
                            parameter("events", supportedEvents.joinToString(separator = ","))
                            if (lastSeenId > 0) parameter("since", lastSeenId)
                        }
                    }

                    val events = eventResponse.body<List<Event<EventData>>>().sortedBy { it.id }
                    events.lastOrNull()?.id?.let { lastSeenId = it }
                    if (events.isNotEmpty()) events.forEach { _eventsSharedFlow.emit(it) }
                } catch (e: Exception) {
                    if (e !is HttpRequestTimeoutException) {
                        logger.e(TAG + "SharedFlow", "Failed to get an Event: ${e.message}", e)
                    }
                    if (e is ConnectException) {
                        sharingJob = null
                        cancel()
                        delay(100_000)
                    }
                } finally {
                    delay(timeMillis = 10)
                }
            }
        }
    }

    companion object {
        private const val TAG = "EventsApi"
    }
}
