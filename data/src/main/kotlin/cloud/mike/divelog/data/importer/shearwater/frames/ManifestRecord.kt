package cloud.mike.divelog.data.importer.shearwater.frames

import cloud.mike.divelog.data.importer.uInt16B
import cloud.mike.divelog.data.importer.uInt32B
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

private const val TYPE_DIVE = 0xA5C4
private const val TYPE_DELETED = 0x5A23

internal sealed interface ManifestRecord {
    data object Unknown : ManifestRecord
    data object Deleted : ManifestRecord
    data class Dive(val timestamp: LocalDateTime, val diveAddress: Long) : ManifestRecord

    companion object {
        const val SIZE_BYTES = 32
    }
}

internal fun ByteArray.parseManifestRecords(): List<ManifestRecord> = this.toList()
    .chunked(ManifestRecord.SIZE_BYTES)
    .map { chunk -> chunk.toByteArray().parseManifestRecord() }

// Example:
//  0: A5 C4       # Record type
//  2: ??          # Unknown data
//  3: ??          # Unknown data
//  4: 64 EA 7C 40 # Epoch seconds
// ...
// 20: 00 00 00 00 # Pointer to dive records
// ...
// 31: ??          # Unknown data
private fun ByteArray.parseManifestRecord(): ManifestRecord = when (uInt16B(0)) {
    TYPE_DELETED -> ManifestRecord.Deleted
    TYPE_DIVE -> ManifestRecord.Dive(
        timestamp = uInt32B(4).seconds.toUtcLocalDate(),
        diveAddress = uInt32B(20),
    )
    else -> ManifestRecord.Unknown
}

internal fun Duration.toUtcLocalDate() = Instant
    .ofEpochMilli(this.inWholeMilliseconds)
    .atZone(ZoneOffset.UTC)
    .toLocalDateTime()