package syncthingrest.api

import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import kotlinx.coroutines.flow.mapNotNull
import syncthingrest.RestApiKt
import syncthingrest.logging.Logger
import syncthingrest.model.folder.Folder
import syncthingrest.model.folder.events.FolderCompletionEvent

class FoldersApi(
    private val restApi: RestApiKt,
    eventsApi: EventsApi,
    private val logger: Logger = Logger
) {
    val folderCompletionEventFlow = eventsApi.eventsFlow.mapNotNull { events ->
        events.filterIsInstance<FolderCompletionEvent>().ifEmpty { null }
    }

    companion object {
        private const val TAG = "FoldersApi"
    }

    suspend fun getFolders(): List<Folder> = try {
        restApi.client.get("rest/config/folders").body<List<Folder>>()
    } catch (e: Exception) {
        logger.e(TAG, "Failed to load folders: ${e.message}", e)
        emptyList()
    }

    suspend fun updateFolders(folders: List<Folder>) = try {
        restApi.client.put("rest/config/folders") {
            setBody(folders)
        }.body<String>()
    } catch (e: Exception) {
        logger.e(TAG, "Failed to put folders: ${e.message}", e)
    }

    suspend fun updateFolders(vararg folders: Folder) = updateFolders(folders = folders.toList())

    suspend fun updateFolder(folder: Folder) = updateFolders(folders = listOf(folder))
}
