package cloud.mike.divelog.data.importer.ostc.frames

import cloud.mike.divelog.data.importer.uInt16L
import cloud.mike.divelog.data.importer.uInt8
import java.time.LocalDateTime
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

internal data class DiveHeaderFull(
    val timestamp: LocalDateTime,
    val maxDepthCentimeters: Int,
    val duration: Duration,
    val minTemperatureCelsius: Float,
    // Most fields omitted because we currently don't need them
) {
    companion object {
        const val SIZE_BYTES = 256
    }
}

internal fun ByteArray.parseFullHeader() = DiveHeaderFull(
    timestamp = LocalDateTime.of(
        uInt8(12) + 2000,
        uInt8(13),
        uInt8(14),
        uInt8(15),
        uInt8(16),
    ),
    maxDepthCentimeters = uInt16L(17),
    duration = uInt16L(19).minutes + uInt8(21).seconds,
    minTemperatureCelsius = uInt16L(22) / 10f,
)
