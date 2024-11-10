package cloud.mike.divelog.data.importer.shearwater

import cloud.mike.divelog.bluetooth.connector.Connection
import cloud.mike.divelog.data.importer.uInt8
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.flow.toList
import java.util.UUID

// Shearwater devices use a single serialPort characteristic for receiving (NOTIFY) and sending (WRITE) messages.
//
// Messages send to the device or received from the device are split into multiple frames and look like this:
// <Frame header> (eg. frame number)
// <Packet header> (e.g. payload size)
// <Command header> (e.g. command byte and parameters)
// <Command payload> (e.g. a block of memory)
// 0xC0 (only on the last frame, this will be escaped in data between frame header an end of frame)
//
// Note: The device tries to send up to 77 bytes per packet, which is larger than the 20 bytes MTU limit, so we have to
// negotiate a higher MTU after connecting:
// https://www.b4x.com/android/forum/threads/ble2-dataavailable-buffer-truncated-after-20-bytes.110344/

private val serialPort = UUID.fromString("27b7570b-359e-45a3-91bb-cf7e70049bd2") // WRITE / NOTIFY
private const val END = 0xC0.toByte()
private const val ESC = 0xDB.toByte()
private const val ESC_END = 0xDC.toByte()
private const val ESC_ESC = 0xDD.toByte()
private const val MAX_FRAME_SIZE = 32

internal suspend fun Connection.downloadMemory(address: Long, sizeBytes: Int): ByteArray {
    var bytes = byteArrayOf()
    var block = 1
    startDownload(address = address, sizeBytes = sizeBytes)
    while (bytes.size < sizeBytes) {
        bytes += fetchBlock(block)
        block++
    }
    endDownload()
    return bytes
}

private suspend fun Connection.startDownload(address: Long, sizeBytes: Int) {
    // 0: 35          # Set compression
    // 1: 01          # Enabled
    // 2: 34          # Set address and size
    // 3: AA AA AA AA # Address
    // 7: SS SS SS    # Size
    val request = byteArrayOf(
        0x35,
        0x00, // TODO implement compression if this is a bottleneck
        0x34,
        (address shr 24).toByte(),
        (address shr 16).toByte(),
        (address shr 8).toByte(),
        (address).toByte(),
        (sizeBytes shr 16).toByte(),
        (sizeBytes shr 8).toByte(),
        (sizeBytes).toByte(),
    )
    // 0: 75 # Command echo
    // 1: 10 # ?
    // 2: PP # Command size
    val response = sendCommand(request)
    response.run {
        // TODO This returns 7F 35 31 after download dive. What does it mean?
        require(size == 3)
        require(uInt8(0) == 0x75)
        require(uInt8(1) == 0x10)
    }
}

private suspend fun Connection.fetchBlock(block: Int): ByteArray {
    // 0: 36 # Fetch block
    val request = byteArrayOf(0x36, block.toByte())
    // 0: 76 # Command echo
    // 1: BB # Block number
    // <Block data>
    val response = sendCommand(request)
    response.run {
        require(size >= 2)
        require(uInt8(0) == 0x76)
        require(uInt8(1) == block)
    }
    return response.drop(2).toByteArray() // Drop command header
}

private suspend fun Connection.endDownload() {
    // 0: 37 # End download
    val request = byteArrayOf(0x37)
    // 0: 77 # Command echo
    // 1: 00 # ?
    val response = sendCommand(request)
    response.run {
        require(size == 2)
        require(uInt8(0) == 0x77)
        require(uInt8(1) == 0x00)
    }
}

private suspend fun Connection.sendCommand(payload: ByteArray): ByteArray {
    // 0: FF # ?
    // 1: 01 # ?
    // 2: SS # Packet size
    // 3: 00 # ?
    // <Packet data>
    val request = byteArrayOf(
        0xFF.toByte(),
        0x01,
        (payload.size + 1).toByte(),
        0x00,
        *payload,
    )
    // 0: 01 # ?
    // 1: FF # ?
    // 2: SS # Packet size
    // 3: 00 # ?
    // <Packet data>
    val response = collectFrames { writeFrames(request) }
    response.run {
        require(size >= 4)
        require(uInt8(0) == 0x01)
        require(uInt8(1) == 0xFF)
        require(uInt8(3) == 0x00)
    }
    val packetLength = response.uInt8(2)
    return response.copyOfRange(4, 4 + packetLength - 1) // Drop packet header
}

private suspend fun Connection.writeFrames(payload: ByteArray) {
    val withDelimiter = payload.escaped() + END
    val chunks = withDelimiter.toList()
        .chunked(MAX_FRAME_SIZE - 2) // Frames have 2 extra header bytes
        .map { it.toByteArray() }
    val frames = chunks.mapIndexed { index, chunk ->
        // 0: TT # Total frames
        // 1: CC # Current frame
        // <Payload>
        byteArrayOf(
            chunks.size.toByte(),
            index.toByte(),
            *chunk,
        )
    }
    frames.forEach { write(serialPort, it) }
}

@OptIn(ExperimentalCoroutinesApi::class)
private suspend fun Connection.collectFrames(onSubscribed: suspend () -> Unit) =
    setupNotification(serialPort, onSetupCompleted = onSubscribed)
        .map { it.drop(2) } // Drop frame header
        .flatMapConcat { it.asFlow() } // Unflatten to Flow<Byte>
        .takeWhile { it != END }
        .toList()
        .toByteArray()
        .unescaped()

private fun ByteArray.escaped() = flatMap { byte ->
    when (byte) {
        END -> listOf(ESC, ESC_END)
        ESC -> listOf(ESC, ESC_ESC)
        else -> listOf(byte)
    }
}.toByteArray()

private fun ByteArray.unescaped(): ByteArray = buildList {
    var escape = false
    for (byte in this@unescaped) {
        when {
            byte == ESC -> escape = true
            escape -> {
                add(byte.unescaped())
                escape = false
            }
            else -> add(byte)
        }
    }
}.toByteArray()

private fun Byte.unescaped() = when (this) {
    ESC_END -> END
    ESC_ESC -> ESC
    else -> this
}
