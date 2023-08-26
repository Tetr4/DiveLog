package cloud.mike.divelog.data.importer.ostc.frames

import cloud.mike.divelog.data.importer.uInt16L
import cloud.mike.divelog.data.importer.uInt24L
import cloud.mike.divelog.data.importer.uInt8
import java.time.LocalDateTime
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

internal data class DiveHeaderCompact(
    val profileNumber: Int,
    val profileSize: Int,
    val timestamp: LocalDateTime,
    val maxDepthCentimeters: Int,
    val diveTime: Duration,
) {
    companion object {
        const val SIZE_BYTES = 16
    }
}

internal fun ByteArray.parseCompactHeaders(): List<DiveHeaderCompact> = this.toList()
    .chunked(DiveHeaderCompact.SIZE_BYTES)
    .mapIndexedNotNull { index, chunk ->
        chunk.toByteArray().parseCompactHeader(profileNumber = index)
    }

// Example:
//  0: 2C 18 00 # 6188 bytes in profile
//  3: 15 02 0C # 2021-02-12
//  6: 0C 0C    # 12:12
//  8: 40 1A    # 6720 -> 67,20m
// 10: 21 00 34 # 33m52s
// 13: 01 00    # Dive Number
// 15: 24       # Profile Version
private fun ByteArray.parseCompactHeader(profileNumber: Int): DiveHeaderCompact? {
    if (this.isEmptyBank) return null
    return DiveHeaderCompact(
        profileNumber = profileNumber,
        profileSize = uInt24L(0),
        timestamp = LocalDateTime.of(
            uInt8(3) + 2000,
            uInt8(4),
            uInt8(5),
            uInt8(6),
            uInt8(7),
        ),
        maxDepthCentimeters = uInt16L(8),
        diveTime = uInt16L(10).minutes + uInt8(12).seconds,
    )
}

private val ByteArray.isEmptyBank
    get() = all { it == 0xFF.toByte() }