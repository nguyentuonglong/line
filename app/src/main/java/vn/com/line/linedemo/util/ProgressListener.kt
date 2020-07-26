package vn.com.line.linedemo.util

internal interface ProgressListener {
    fun update(
        bytesRead: Long,
        contentLength: Long,
        done: Boolean
    )
}