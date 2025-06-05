package syncthingrest.logging

actual object Logger {
    private fun log(level: Level, tag: String, message: String, e: Exception?) {
        println("# $tag @ $level: $message")
        e?.let { e ->
            println("Exception: ${e::class.qualifiedName}")
            e.message?.let { message -> println(message) }
            println()
            e.printStackTrace()
        }
    }

    actual fun e(tag: String, message: String, e: Exception?) {
        log(Level.E, tag, message, e)
    }

    actual fun w(tag: String, message: String, e: Exception?) {
        log(Level.W, tag, message, e)
    }

    actual fun i(tag: String, message: String, e: Exception?) {
        log(Level.I, tag, message, e)
    }

    actual fun d(tag: String, message: String, e: Exception?) {
        log(Level.D, tag, message, e)
    }

    actual fun v(tag: String, message: String, e: Exception?) {
        log(Level.V, tag, message, e)
    }

    actual fun wtf(tag: String, message: String, e: Exception?) {
        log(Level.WTF, tag, message, e)
    }
}

private enum class Level {
    E, W, I, D, V, WTF
}
