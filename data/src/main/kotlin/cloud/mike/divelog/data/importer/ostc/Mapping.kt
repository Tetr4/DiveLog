package cloud.mike.divelog.data.importer.ostc

import cloud.mike.divelog.data.dives.DepthProfile
import cloud.mike.divelog.data.dives.Dive
import cloud.mike.divelog.data.importer.ostc.frames.Profile
import java.util.UUID

internal fun Profile.toDive(number: Int) = Dive(
    id = UUID.randomUUID(),
    number = number,
    location = null,
    start = diveHeader.timestamp,
    diveTime = diveHeader.diveTime,
    maxDepthMeters = diveHeader.maxDepthCentimeters / 100f,
    minTemperatureCelsius = diveHeader.minTemperatureCelsius,
    depthProfile = DepthProfile(
        id = UUID.randomUUID(),
        samplingRate = profileHeader.samplingRate,
        depthCentimeters = samples.map { it.depthCentimeters }.toIntArray(),
    ),
    notes = null,
)
