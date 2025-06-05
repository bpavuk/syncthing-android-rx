package syncthingrest

import java.io.File
import javax.net.ssl.X509TrustManager

actual interface SslSettings {
    actual fun getTrustManager(): X509TrustManager
}

class AndroidSslSettings(val httpsCertFile: File) : SslSettings {
    override fun getTrustManager(): X509TrustManager = SyncthingTrustManager(httpsCertFile)
}