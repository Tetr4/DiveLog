package cloud.mike.divelog.data.dives

import cloud.mike.divelog.data.importer.ostc.frames.DiveHeaderCompactFrame
import cloud.mike.divelog.data.importer.ostc.frames.DiveProfileFrame
import java.util.UUID
import kotlin.time.Duration.Companion.seconds

internal fun DiveHeaderCompactFrame.toDive(number: Int) = Dive(
    id = UUID.randomUUID(),
    number = number,
    location = null,
    start = timestamp,
    diveTime = diveTime,
    maxDepthMeters = maxDepthCentimeters / 100f,
    minTemperatureCelsius = null,
    depthProfile = null,
    notes = null,
)

internal fun DiveProfileFrame.toDive(number: Int) = Dive(
    id = UUID.randomUUID(),
    number = number,
    location = null,
    start = diveHeader.timestamp,
    diveTime = diveHeader.diveTime,
    maxDepthMeters = diveHeader.maxDepthCentimeters / 100f,
    minTemperatureCelsius = diveHeader.minTemperatureCelsius,
    depthProfile = DepthProfile(
        samplingRate = profileHeader.samplingRateSeconds.seconds,
        depthCentimeters = samples.map { it.depthCentimeters }.toIntArray(),
    ),
    notes = null,
)
