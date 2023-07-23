package cloud.mike.divelog.data.importer

// Payload is in little endian
internal fun ByteArray.uInt8(index: Int) = this[index].toUByte().toInt()
internal fun ByteArray.uInt16(index: Int) = (uInt8(index + 1) shl 8) or uInt8(index)
internal fun ByteArray.uInt24(index: Int) = (uInt8(index + 2) shl 16) or (uInt8(index + 1) shl 8) or uInt8(index)
internal fun Byte.flag(index: Int) = (this.toInt() and (1 shl index)) > 0
