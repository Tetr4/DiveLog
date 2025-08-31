package cloud.mike.divelog.bluetooth.utils

import android.content.Intent
import android.os.Build

@Suppress("DEPRECATION")
internal inline fun <reified T> Intent.getParcelableExtraCompat(name: String) = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> getParcelableExtra(name, T::class.java)
    else -> getParcelableExtra(name)
}
