package cloud.mike.divelog.data.importer.frames

import cloud.mike.divelog.data.importer.uInt16
import cloud.mike.divelog.data.importer.uInt8
import java.time.LocalDateTime
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

data class FullHeaderFrame(
    val timestamp: LocalDateTime,
    val maxDepthCentimeters: Int,
    val diveTime: Duration,
    val minTemperatureCelsius: Float,
    // most fields omitted because we don't need them
) {
    internal companion object {
        const val SIZE_BYTES = 256
    }
}

internal fun ByteArray.parseFullHeader() = FullHeaderFrame(
    timestamp = LocalDateTime.of(
        uInt8(12) + 2000,
        uInt8(13),
        uInt8(14),
        uInt8(15),
        uInt8(16),
    ),
    maxDepthCentimeters = uInt16(17),
    diveTime = uInt16(19).minutes + uInt8(21).seconds,
    minTemperatureCelsius = uInt16(22) / 10f,
)
