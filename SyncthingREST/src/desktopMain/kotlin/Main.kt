
import kotlinx.coroutines.flow.merge
import kotlinx.serialization.ExperimentalSerializationApi
import syncthingrest.DesktopSslSettings
import syncthingrest.RestApiKt

@OptIn(ExperimentalSerializationApi::class)
suspend fun main() {
    val api = RestApiKt(
        apiKey = System.getenv("SYNCTHING_API_KEY"),
        baseUrl = System.getenv("SYNCTHING_ADDR") ?: "http://127.0.0.1:8384/",
        sslSettings = DesktopSslSettings()
    )

    merge(
        api.devices.deviceConnectedEventFlow,
        api.devices.deviceDisconnectedEventFlow,
        api.devices.deviceDiscoveredEventFlow,
        api.devices.deviceResumedEventFlow,
        api.folders.folderCompletionEventFlow
    ).collect {
        println(it)
    }
}
