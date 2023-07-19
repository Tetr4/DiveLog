package cloud.mike.divelog.data.communication.frames

import cloud.mike.divelog.data.communication.uInt16
import cloud.mike.divelog.data.communication.uInt24
import cloud.mike.divelog.data.communication.uInt8
import java.time.LocalDateTime
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

data class CompactHeaderFrame(
    val profileNumber: Int,
    val profileSize: Int,
    val timestamp: LocalDateTime,
    val maxDepthCentimeters: Int,
    val diveTime: Duration,
)

// Example:
//  0: 2C 18 00 # 6188 bytes in profile
//  3: 15 02 0C # 21-02-12
//  6: 0C 0C    # 12:12
//  8: 40 1A    # 6720 -> 67,20m
// 10: 21 00 34 # 33m52s
// 13: 01 00    # Dive Number
// 15: 24       # Profile Version
internal fun ByteArray.parseCompactHeader(profileNumber: Int): CompactHeaderFrame? {
    if (this.isEmptyBank) return null
    return CompactHeaderFrame(
        profileNumber = profileNumber,
        profileSize = uInt24(0),
        timestamp = LocalDateTime.of(
            uInt8(3) + 2000,
            uInt8(4),
            uInt8(5),
            uInt8(6),
            uInt8(7),
        ),
        maxDepthCentimeters = uInt16(8),
        diveTime = uInt16(10).minutes + uInt8(12).seconds,
    )
}

internal fun ByteArray.parseCompactHeaders(): List<CompactHeaderFrame> = this.toList()
    .chunked(16)
    .mapIndexedNotNull { index, chunk ->
        chunk.toByteArray().parseCompactHeader(profileNumber = index) // TODO check order
    }

private val ByteArray.isEmptyBank
    get() = all { it == 0xFF.toByte() }