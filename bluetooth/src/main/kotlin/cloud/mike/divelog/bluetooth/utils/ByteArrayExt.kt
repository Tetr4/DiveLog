package cloud.mike.divelog.bluetooth.utils

import java.util.Locale

fun Byte.toHexString() = String.format(Locale.ENGLISH, "%02X", this)
fun ByteArray.toHexString() = joinToString { it.toHexString() }
