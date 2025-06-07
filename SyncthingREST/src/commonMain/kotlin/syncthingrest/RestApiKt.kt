package syncthingrest

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import syncthingrest.api.DevicesApi
import syncthingrest.api.FoldersApi
import syncthingrest.logging.Logger
import javax.net.ssl.X509TrustManager


class RestApiKt(
    private val baseUrl: String,
    private val apiKey: String,
    private val sslSettings: SslSettings,
    private val logger: Logger = Logger,
) {
    val client = HttpClient(CIO) {
        expectSuccess = true // Ktor will throw exceptions for non-2xx responses
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
            })
        }
        install(Logging)
        defaultRequest {
            url(this@RestApiKt.baseUrl)
            header("X-API-Key", this@RestApiKt.apiKey)
            contentType(ContentType.Application.Json)
        }

        engine {
            https {
                trustManager = sslSettings.getTrustManager()
            }
        }
    }

    val folders = FoldersApi(restApi = this)
    val devices = DevicesApi(restApi = this)

    companion object {
        private const val TAG = "RestApiKt"
    }
}
expect interface SslSettings {
    fun getTrustManager(): X509TrustManager
}
