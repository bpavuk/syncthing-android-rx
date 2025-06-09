
import kotlinx.coroutines.flow.merge
import kotlinx.serialization.ExperimentalSerializationApi
import syncthingrest.DesktopSslSettings
import syncthingrest.RestApiKt
import syncthingrest.model.folder.Folder
import syncthingrest.model.folder.FolderID

@OptIn(ExperimentalSerializationApi::class)
suspend fun main() {
    val api = RestApiKt(
        apiKey = System.getenv("SYNCTHING_API_KEY"),
        baseUrl = System.getenv("SYNCTHING_ADDR") ?: "http://127.0.0.1:8384/",
        sslSettings = DesktopSslSettings()
    )

    val folder = Folder(
        FolderID("kotlin-test-folder-syncthing-api"),
        path = "~/KotlinTestFolderSyncthingAPI"
    )

    println("Adding folder...")
    api.folders.addFolder(folder).getOrThrow()
    println()

    println("Deleting folder...")
    val folderResponse = api.folders.deleteFolder(folder.id).getOrThrow()
    println("Got 404: ${folderResponse == null}")
    println()

    println("Listening to the events...")
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
