package cloud.mike.divelog.data.importer.shearwater.frames

import cloud.mike.divelog.data.importer.uInt16B
import cloud.mike.divelog.data.importer.uInt24B
import cloud.mike.divelog.data.importer.uInt32B
import cloud.mike.divelog.data.importer.uInt8
import java.time.LocalDateTime
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

private const val FEET_PER_METER = 0.3048f
private const val RECORD_SIZE_BYTES = 32

// We only need these types
private const val TYPE_DIVE_SAMPLE = 0x01.toByte()
private const val TYPE_OPENING_0 = 0x10.toByte()
private const val TYPE_OPENING_5 = 0x15.toByte()
private const val TYPE_CLOSING_0 = 0x20.toByte()

internal data class DiveData(
    val timestamp: LocalDateTime,
    val duration: Duration,
    val maxDepthMeters: Float,
    val samplingRate: Duration,
    val samples: List<Sample>,
) {
    data class Sample(
        val depthMeters: Float,
    )
}

// Example (order not guaranteed):
// <Opening 0>
// <Opening 1>
// ...
// <Sample>
// <Sample>
// ...
// <Closing 0>
// <Closing 1>
// ...
internal fun ByteArray.parseDiveData(): DiveData {
    val opening0 = getRecord(TYPE_OPENING_0) ?: error("Missing record ($TYPE_OPENING_0)")
    val opening5 = getRecord(TYPE_OPENING_5)
    val closing0 = getRecord(TYPE_CLOSING_0) ?: error("Missing record ($TYPE_CLOSING_0)")
    val imperial = opening0.uInt8(8) == 1
    return DiveData(
        timestamp = opening0.uInt32B(12).seconds.toUtcLocalDate(),
        samplingRate = opening5?.uInt16B(23)?.milliseconds ?: 10.seconds,
        duration = closing0.uInt24B(6).seconds,
        maxDepthMeters = closing0.uInt16B(4).parseDepthMeters(imperial),
        samples = parseSamples(imperial = imperial),
    )
}

private fun ByteArray.getRecord(type: Byte): ByteArray? = this.asSequence()
    .chunked(RECORD_SIZE_BYTES)
    .find { it.first() == type }
    ?.toByteArray()

private fun ByteArray.parseSamples(imperial: Boolean): List<DiveData.Sample> = this.asSequence()
    .chunked(RECORD_SIZE_BYTES)
    .filter { it.first() == TYPE_DIVE_SAMPLE }
    .map { it.toByteArray().parseSample(imperial = imperial) }
    .toList()

private fun ByteArray.parseSample(imperial: Boolean) = DiveData.Sample(
    depthMeters = uInt16B(1).parseDepthMeters(imperial),
)

private fun Int.parseDepthMeters(imperial: Boolean): Float =
    (this / 10f) * if (imperial) FEET_PER_METER else 1f // stored as 1/10 m or ft
