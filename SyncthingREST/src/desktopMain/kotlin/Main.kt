
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import syncthingrest.DesktopSslSettings
import syncthingrest.RestApiKt

@OptIn(ExperimentalSerializationApi::class)
suspend fun main() {
    val api = RestApiKt(
        apiKey = System.getenv("SYNCTHING_API_KEY"),
        baseUrl = System.getenv("SYNCTHING_ADDR") ?: "http://127.0.0.1:8384/",
        sslSettings = DesktopSslSettings()
    )

    val folders = api.folders.getFolders()
    val json = Json {
        prettyPrint = true
        prettyPrintIndent = "  "
        encodeDefaults = true
    }
    println(json.encodeToString(folders))

    val firstFolder = folders.first()
    val patchedFolderResponse = api.folders.updateFolder(firstFolder.copy(label = "Fuckery"))
    println(patchedFolderResponse)
}
