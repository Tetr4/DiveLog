package cloud.mike.divelog.data.communication

import java.time.Duration
import java.time.LocalDateTime

data class DiveHeader(
    val profileSize: Int,
    val timestamp: LocalDateTime,
    val depthMeters: Float,
    val diveTime: Duration,
) {
    companion object {
        fun fromPayload(bytes: ByteArray) = bytes.toList()
            .chunked(16)
            .map { it.toByteArray() }
            .filter { !it.isEmptyBank }
            .map { fromBytes(it) }

        // Example:
        //  0: 2C 18 00 # 6188 (profile size)
        //  3: 15 02 0C # 21-02-12
        //  6: 0C 0C    # 12:12
        //  8: 40 1A    # 6720 -> 67,20m
        // 10: 21 00 34 # 33m52s
        // 13: 01 00    # Dive Number
        // 15: 24       # logbook profile version
        private fun fromBytes(bytes: ByteArray) = DiveHeader(
            profileSize = bytes.uInt24(0),
            timestamp = LocalDateTime.of(
                bytes.uInt8(3) + 2000,
                bytes.uInt8(4),
                bytes.uInt8(5),
                bytes.uInt8(6),
                bytes.uInt8(7),
            ),
            depthMeters = bytes.uInt16(8) / 100f,
            diveTime = Duration.ofMinutes(bytes.uInt16(10).toLong()) + Duration.ofSeconds(bytes.uInt8(12).toLong()),
        )
    }
}

private val ByteArray.isEmptyBank
    get() = all { it == 0xFF.toByte() }