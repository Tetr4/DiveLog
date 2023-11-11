package cloud.mike.divelog.data.importer.ostc

import cloud.mike.divelog.data.dives.Dive
import cloud.mike.divelog.data.dives.DiveProfile
import cloud.mike.divelog.data.importer.ostc.frames.Profile
import java.util.UUID

internal fun Profile.toDive(number: Int) = Dive(
    id = UUID.randomUUID(),
    number = number,
    startDate = diveHeader.timestamp.toLocalDate(),
    startTime = diveHeader.timestamp.toLocalTime(),
    duration = diveHeader.duration,
    maxDepthMeters = diveHeader.maxDepthCentimeters / 100f,
    minTemperatureCelsius = diveHeader.minTemperatureCelsius,
    location = null,
    profile = DiveProfile(
        id = UUID.randomUUID(),
        samplingRate = profileHeader.samplingRate,
        depthCentimeters = samples.map { it.depthCentimeters }.toIntArray(),
    ),
    notes = null,
)
