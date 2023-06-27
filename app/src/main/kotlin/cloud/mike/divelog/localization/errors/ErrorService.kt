package cloud.mike.divelog.localization.errors

import android.content.Context
import androidx.annotation.StringRes
import cloud.mike.divelog.R
import java.net.ConnectException
import java.net.SocketException
import java.net.UnknownHostException
import java.util.concurrent.CancellationException
import javax.net.ssl.SSLException

class ErrorService(private val context: Context) {

    fun createMessage(throwable: Throwable) = ErrorMessage(
        content = throwable.localize(),
    )

    private fun Throwable.localize(): String = when (this) {
        is UnknownHostException, is ConnectException,
        is SocketException, is SSLException,
        -> get(R.string.common_error_connectivity)

        is CancellationException -> localize()
        else -> genericMessage()
    }

    private fun CancellationException.localize() = cause?.localize() ?: genericMessage()

    private fun Throwable.genericMessage() = message ?: this::class.java.simpleName

    private fun get(@StringRes resId: Int) = context.getString(resId)
}
