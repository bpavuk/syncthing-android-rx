package syncthingrest.logging

expect class Logger {
    fun e(tag: String, message: String, e: Exception? = null)
    fun w(tag: String, message: String, e: Exception? = null)
    fun i(tag: String, message: String, e: Exception? = null)
    fun d(tag: String, message: String, e: Exception? = null)
    fun v(tag: String, message: String, e: Exception? = null)
    fun wtf(tag: String, message: String, e: Exception? = null)
}
