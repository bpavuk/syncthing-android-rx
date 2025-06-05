package syncthingrest.logging

import android.util.Log

actual object Logger {
    actual fun e(tag: String, message: String, e: Exception?) {
        e?.let { Log.e(tag, message, it) } ?: Log.e(tag, message)
    }

    actual fun w(tag: String, message: String, e: Exception?) {
        e?.let { Log.w(tag, message, it) } ?: Log.w(tag, message)
    }

    actual fun i(tag: String, message: String, e: Exception?) {
        e?.let { Log.i(tag, message, it) } ?: Log.i(tag, message)
    }

    actual fun d(tag: String, message: String, e: Exception?) {
        e?.let { Log.d(tag, message, it) } ?: Log.d(tag, message)
    }

    actual fun v(tag: String, message: String, e: Exception?) {
        e?.let { Log.v(tag, message, it) } ?: Log.v(tag, message)
    }

    actual fun wtf(tag: String, message: String, e: Exception?) {
        e?.let { Log.wtf(tag, message, it) } ?: Log.wtf(tag, message)
    }
}
