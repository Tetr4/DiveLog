package cloud.mike.divelog.data.importer.ostc.frames

import cloud.mike.divelog.data.importer.uInt16L
import cloud.mike.divelog.data.importer.uInt8

internal data class ProfileSample(
    val depthCentimeters: Int,
)

internal fun ByteArray.parseProfileSamples(): List<ProfileSample> {
    val samples = mutableListOf<ProfileSample>()
    var index = 0
    while (index < lastIndex) {
        val sampleSize = this.sampleSize(index)
        samples += this
            .copyOfRange(index, index + sampleSize)
            .parseProfileSample()
        index += sampleSize
    }
    return samples
}

private fun ByteArray.sampleSize(sampleStartIndex: Int): Int {
    // Sample size is dynamic, so read it from ProfileFlagByte
    val profileFlagByte = uInt8(sampleStartIndex + 2)
    val remainingBytes = profileFlagByte and 0b0111_1111
    return 3 + remainingBytes // Add 3 bytes, because remainingBytes only counts bytes after the profileFlagByte
}

// Depth:
// 0: Depth Low
// 1: Depth High
//
// Event flags:
// 2: ProfileFlagByte (ELLLL LLLL) <- (E)ventByte follows | (L)ength of remaining bytes
// 3: EventByte1 (ESGM AAAA) <- (E)ventByte follows | (S)etpoint Change | (G)as Change | (M)anual Gas | (A)larm
// 4: EventByte2 (E--- ---B) <- (E)ventByte follows | (B)ailout
// ...
//
// Event data bytes (depending on flags, always in the following order):
// - (M)anual Gas Set 02%
// - (M)anual Gas Set He%
// - (G)as Change
// - (S)etpoint Change
// - (B)ailout O2%
// - (B)ailout He%
//
// Extended info bytes (depending on divisors, always in the following order):
// - Temperature Low
// - Temperature High
// - Deco Stop Depth
// - Deco Stop Time or NDL
// - Gradient Factor
// - PPO2 Sensor 1
// - PPO2 Sensor 1 Voltage Low
// - PP02 Sensor 1 Voltage High
// - PPO2 Sensor 2
// - PPO2 Sensor 2 Voltage Low
// - PP02 Sensor 2 Voltage High
// - PPO2 Sensor 3
// - PPO2 Sensor 3 Voltage Low
// - PP02 Sensor 3 Voltage High
// - Deco Stop 1 Duration
// - ...
// - Deco Stop 15 Duration
// - CNS Low
// - CNS High
// - Tank Pressure Low
// - Tank Pressure High
private fun ByteArray.parseProfileSample() = ProfileSample(
    depthCentimeters = uInt16L(0),
)
