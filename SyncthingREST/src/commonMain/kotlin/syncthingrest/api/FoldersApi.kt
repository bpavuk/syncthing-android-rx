package syncthingrest.api

import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.mapNotNull
import syncthingrest.RestApiKt
import syncthingrest.logging.Logger
import syncthingrest.model.folder.Folder
import syncthingrest.model.folder.FolderID
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


    suspend fun getFolders(): Result<List<Folder>> {
        return try {
            Result.success(restApi.client.get("rest/config/folders").body<List<Folder>>())
        } catch (e: Exception) {
            logger.e(TAG, "Failed to load folders: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun getFolder(id: FolderID): Result<Folder?> {
        return try {
            val response = restApi.client.get("rest/config/folders/${id.value}")
            if (response.status == HttpStatusCode.NotFound) {
                Result.success(null)
            } else {
                Result.success(response.body<Folder>())
            }
        } catch (e: Exception) {
            logger.e(TAG, "Failed to get folder id ${id.value}: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun updateFolders(folders: List<Folder>): Result<Unit> {
        return try {
            restApi.client.put("rest/config/folders") {
                setBody(folders)
            }.body<String>()
            Result.success(Unit)
        } catch (e: Exception) {
            logger.e(TAG, "Failed to put folders: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun updateFolders(vararg folders: Folder) = updateFolders(folders = folders.toList())

    suspend fun updateFolder(folder: Folder) = updateFolders(folders = listOf(folder))

    suspend fun addFolders(folders: List<Folder>): Result<Unit> {
        return try {
            val allFolders = getFolders().getOrThrow()
            val pureFolders = folders.filterNot { folder ->
                folder in allFolders
            }
            Result.success(updateFolders(pureFolders).getOrThrow())
        } catch (e: Exception) {
            logger.e(TAG, "Failed to add folders: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun addFolders(vararg folders: Folder) = addFolders(folders = folders.toList())

    suspend fun addFolder(folder: Folder) = addFolders(folders = listOf(folder))

    suspend fun deleteFolder(id: FolderID): Result<Unit?> {
        return try {
            val response = restApi.client.delete("rest/config/folders/${id.value}")
            if (response.status == HttpStatusCode.NotFound) {
                Result.success(null)
            } else {
                Result.success(Unit)
            }
        } catch (e: Exception) {
            logger.e(TAG, "Failed to delete folder: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun pauseFolder(id: FolderID): Result<Unit> {
        return try {
            restApi.client.patch("rest/config/folders/${id.value}") {
                setBody(mapOf("paused" to true))
            }.body<String>()
            Result.success(Unit)
        } catch (e: Exception) {
            logger.e(TAG, "Failed to pause folder ${id.value}: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun pauseFolder(folder: Folder): Result<Unit> = pauseFolder(folder.id)

    suspend fun resumeFolder(id: FolderID): Result<Unit> {
        return try {
            restApi.client.patch("rest/config/folders/${id.value}") {
                setBody(mapOf("paused" to false))
            }.body<String>()
            Result.success(Unit)
        } catch (e: Exception) {
            logger.e(TAG, "Failed to resume folder ${id.value}: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun resumeFolder(folder: Folder): Result<Unit> = resumeFolder(folder.id)
}
