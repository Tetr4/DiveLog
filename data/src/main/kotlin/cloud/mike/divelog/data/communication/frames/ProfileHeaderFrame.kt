package cloud.mike.divelog.data.communication.frames

import cloud.mike.divelog.data.communication.uInt24
import cloud.mike.divelog.data.communication.uInt8

data class ProfileHeaderFrame(
    val profileDataLength: Int,
    val samplingRateSeconds: Int,
    val numDivisors: Int,
    val divisorTemperature: Int?,
    val divisorDeco: Int?,
    val divisorGradientFactor: Int?,
    val divisorPpo2Sensor: Int?,
    val divisorDecoPlan: Int?,
    val divisorCnsProfile: Int?,
    val divisorTankData: Int?,
) {
    internal val sizeBytes
        get() = 5 + numDivisors * 3
}

// Example:
//  0: 2C 18 00 # 6188 bytes
//  3: 02       # sampled every 2 seconds
//  4: 07       # 7 divisors
//  5: 00 02 06 # Temperature
//  8: 01 02 06 # Deco
// 11: 02 01 0C # Gradient Factor
// 14: 03 09 00 # PPO2 Sensor
// 17: 04 0F 0C # Deco Plan
// 20: 05 02 0C # CNS
// 23: 06 02 00 # Tank Pressure
internal fun ByteArray.parseProfileHeader(): ProfileHeaderFrame {
    val numDivisors = uInt8(4)
    fun getDivisor(divisor: Int) = if (divisor <= numDivisors) {
        // 0: Data Type
        // 1: Data Length
        // 3: Divisor
        uInt8(2 + 3 * divisor + 2).takeIf { it != 0 }
    } else {
        null
    }
    return ProfileHeaderFrame(
        profileDataLength = uInt24(0),
        samplingRateSeconds = uInt8(3),
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
