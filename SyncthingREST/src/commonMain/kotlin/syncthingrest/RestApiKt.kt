package syncthingrest

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import syncthingrest.logging.Logger
import syncthingrest.model.device.Device

@Serializable
private data class SystemConfig(
    val devices: List<Device>
    // Add other fields from the config if needed, e.g., folders, gui, etc.
)

class RestApiKt(
    private val baseUrl: String,
    private val apiKey: String,
    private val logger: Logger = Logger(),
) {
    private val client: HttpClient = HttpClient(CIO) {
        expectSuccess = true // Ktor will throw exceptions for non-2xx responses
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true // Important for evolving APIs
            })
        }
        defaultRequest {
            url(this@RestApiKt.baseUrl) // Base URL for all requests
            header("X-API-Key", this@RestApiKt.apiKey)
            contentType(ContentType.Application.Json)
        }
    }

    suspend fun loadDevices(): List<Device> { // Return type uses Kotlin Device
        return try {
            logger.d(TAG, "Attempting to load devices from /rest/config/devices")
            val devices = client.get("rest/config/devices").body<List<Device>>()
            logger.i(TAG, "Successfully loaded \${config.devices.size} devices")
            devices
        } catch (e: Exception) {
            logger.e(TAG, "Failed to load devices: \${e.message}", e)
            emptyList() // Return an empty list or rethrow a custom domain-specific exception
        }
    }

    // Placeholder for other API methods, e.g., loadFolders, getStatus, etc.

    companion object {
        private const val TAG = "RestApiKt"
    }
}
