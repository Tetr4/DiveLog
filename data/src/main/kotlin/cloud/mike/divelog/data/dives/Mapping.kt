package cloud.mike.divelog.data.dives

import cloud.mike.divelog.data.communication.frames.CompactHeaderFrame
import cloud.mike.divelog.data.communication.frames.DiveProfileFrame
import java.util.UUID
import kotlin.time.Duration.Companion.seconds

internal fun CompactHeaderFrame.toDive(number: Int) = Dive(
    id = UUID.randomUUID(),
    number = number,
    location = "",
    start = timestamp,
    diveTime = diveTime,
    maxDepthMeters = maxDepthCentimeters / 100f,
    minTemperatureCelsius = null,
    diveProfile = null,
)

internal fun DiveProfileFrame.toDive(number: Int) = Dive(
    id = UUID.randomUUID(),
    number = number,
    location = "",
    start = diveHeader.timestamp,
    diveTime = diveHeader.diveTime,
    maxDepthMeters = diveHeader.maxDepthCentimeters / 100f,
    minTemperatureCelsius = diveHeader.minTemperatureCelsius,
    diveProfile = DiveProfile(
        samplingRate = profileHeader.samplingRateSeconds.seconds,
        depthCentimeters = samples.map { it.depthCentimeters }.toIntArray(),
    ),
)