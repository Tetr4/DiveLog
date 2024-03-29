package cloud.mike.divelog.data.importer.ostc.frames

import cloud.mike.divelog.data.importer.uInt24L
import cloud.mike.divelog.data.importer.uInt8
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

internal data class ProfileHeader(
    val profileDataLength: Int,
    val samplingRate: Duration,
    val numDivisors: Int,
    val divisorTemperature: Int?,
    val divisorDeco: Int?,
    val divisorGradientFactor: Int?,
    val divisorPpo2Sensor: Int?,
    val divisorDecoPlan: Int?,
    val divisorCnsProfile: Int?,
    val divisorTankData: Int?,
) {
    val sizeBytes
        get() = 5 + numDivisors * 3
}

// Example:
//  0: 2C 18 00 # 6188 bytes of profile data
//  3: 02       # Sampled every 2 seconds
//  4: 07       # 7 divisors
//  5: 00 02 06 # Temperature
//  8: 01 02 06 # Deco
// 11: 02 01 0C # Gradient Factor
// 14: 03 09 00 # PPO2 Sensor
// 17: 04 0F 0C # Deco Plan
// 20: 05 02 0C # CNS
// 23: 06 02 00 # Tank Pressure
internal fun ByteArray.parseProfileHeader(): ProfileHeader {
    val numDivisors = uInt8(4)
    fun getDivisor(divisor: Int) = if (divisor <= numDivisors) {
        // 0: Data Type
        // 1: Data Length
        // 3: Divisor
        uInt8(2 + 3 * divisor + 2).takeIf { it != 0 }
    } else {
        null
    }
    return ProfileHeader(
        profileDataLength = uInt24L(0),
        samplingRate = uInt8(3).seconds,
        numDivisors = numDivisors,
        divisorTemperature = getDivisor(1),
        divisorDeco = getDivisor(2),
        divisorGradientFactor = getDivisor(3),
        divisorPpo2Sensor = getDivisor(4),
        divisorDecoPlan = getDivisor(5),
        divisorCnsProfile = getDivisor(6),
        divisorTankData = getDivisor(7),
    )
}
