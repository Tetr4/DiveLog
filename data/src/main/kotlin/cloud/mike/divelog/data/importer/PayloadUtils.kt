package cloud.mike.divelog.data.importer

import java.nio.ByteBuffer

internal fun ByteArray.uInt8(index: Int) = this[index].toUByte().toInt()

// Little Endian
internal fun ByteArray.uInt16L(index: Int) = (uInt8(index + 1) shl 8) or uInt8(index)
internal fun ByteArray.uInt24L(index: Int) = (uInt8(index + 2) shl 16) or (uInt8(index + 1) shl 8) or uInt8(index)

// Big Endian
internal fun ByteArray.uInt16B(index: Int) = (uInt8(index) shl 8) or uInt8(index + 1)
internal fun ByteArray.uInt24B(index: Int) = (uInt8(index) shl 16) or (uInt8(index + 1) shl 8) or uInt8(index + 2)
internal fun ByteArray.uInt32B(index: Int) = ByteBuffer.wrap(this).getInt(index).toUInt().toLong()
