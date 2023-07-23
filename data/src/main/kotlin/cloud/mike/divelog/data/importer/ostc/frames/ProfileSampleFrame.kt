package cloud.mike.divelog.data.importer.ostc.frames

import cloud.mike.divelog.data.importer.uInt16
import cloud.mike.divelog.data.importer.uInt8

internal data class ProfileSampleFrame(
    val depthCentimeters: Int,
    // TODO Parse more data
)

// Depth:
// 0: Depth Low
// 1: Depth High
//
// Event flags:
// 2: ProfileFlagByte (LLLL LLLE) <- (L)ength of remaining bytes | (E)ventByte follows
// 3: EventByte1 (AAAA MGSE) <- (A)larm | (M)anual Gas | (G)as Change | (S)etpoint Change | (E)ventByte follows
// 4: EventByte2 (B--- ---E) <- (B)ailout | (E)ventByte follows
// ...
//
// Event data (depending on flags, always in order):
// - (M)anual Gas Set 02%
// - (M)anual Gas Set He%
// - (G)as Change
// - (S)etpoint Change
// - (B)ailout O2%
// - (B)ailout He%
//
// Extended info (depending on divisors, always in order):
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
internal fun ByteArray.parseProfileSample() = ProfileSampleFrame(
    depthCentimeters = uInt16(0),
)

internal fun ByteArray.parseProfileSamples(): List<ProfileSampleFrame> {
    var index = 0
    val samples = mutableListOf<ProfileSampleFrame>()
    while (index < lastIndex) {
        val remainingBytes = uInt8(index + 2) and 0b0111_1111
        val nextIndex = index + 3 + remainingBytes
        val sampleBytes = copyOfRange(index, nextIndex)
        samples += sampleBytes.parseProfileSample()
        index = nextIndex
    }
    return samples
}
