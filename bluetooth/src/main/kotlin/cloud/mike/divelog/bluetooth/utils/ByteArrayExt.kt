package cloud.mike.divelog.bluetooth.utils

import java.util.Locale

fun ByteArray.toHexString() = joinToString { it.toHexString() }
fun Byte.toHexString() = String.format(Locale.ENGLISH, "%02X", this)
