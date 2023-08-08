@file:OptIn(ExperimentalStdlibApi::class)

package cloud.mike.divelog.bluetooth.utils

private val hexFormat = HexFormat {
    upperCase = true
    bytes { byteSeparator = " " }
}

fun ByteArray.toHexString() = toHexString(hexFormat)
