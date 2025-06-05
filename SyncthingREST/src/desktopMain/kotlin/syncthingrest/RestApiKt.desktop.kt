package syncthingrest

import java.security.cert.X509Certificate
import javax.net.ssl.X509TrustManager

actual interface SslSettings {
    actual fun getTrustManager(): X509TrustManager
}

class DesktopSslSettings() : SslSettings {
    override fun getTrustManager(): X509TrustManager = @Suppress("CustomX509TrustManager")
    object : X509TrustManager {
        @Suppress("TrustAllX509TrustManager")
        override fun checkClientTrusted(
            chain: Array<out X509Certificate?>?,
            authType: String?
        ) {

        }

        @Suppress("TrustAllX509TrustManager")
        override fun checkServerTrusted(
            chain: Array<out X509Certificate?>?,
            authType: String?
        ) {

        }

        override fun getAcceptedIssuers(): Array<out X509Certificate?>? {
            return null
        }
    }
}
