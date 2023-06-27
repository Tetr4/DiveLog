package cloud.mike.divelog.data.logging

import android.util.Log

val Any.TAG: String
    get() = this::class.java.simpleName

fun Any.logError(t: Throwable) {
    Log.e(TAG, t.message ?: t::class.java.simpleName, t)
}
