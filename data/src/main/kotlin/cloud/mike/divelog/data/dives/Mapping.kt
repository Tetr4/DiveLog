package cloud.mike.divelog.data.dives

import cloud.mike.divelog.data.importer.ostc.frames.DiveProfileFrame
import cloud.mike.divelog.persistence.dives.DepthProfileDto
import cloud.mike.divelog.persistence.dives.DiveDto
import cloud.mike.divelog.persistence.dives.DiveSpotDto
import cloud.mike.divelog.persistence.dives.DiveWithLocationAndProfile
import java.util.UUID
import kotlin.time.Duration.Companion.seconds

internal fun DiveProfileFrame.toDive(number: Int) = Dive(
    id = UUID.randomUUID(),
    number = number,
    location = null,
    start = diveHeader.timestamp,
    diveTime = diveHeader.diveTime,
    maxDepthMeters = diveHeader.maxDepthCentimeters / 100f,
    minTemperatureCelsius = diveHeader.minTemperatureCelsius,
    depthProfile = DepthProfile(
        id = UUID.randomUUID(),
        samplingRate = profileHeader.samplingRateSeconds.seconds,
        depthCentimeters = samples.map { it.depthCentimeters }.toIntArray(),
    ),
    notes = null,
)

internal fun DiveWithLocationAndProfile.toEntity() = Dive(
    id = dive.id,
    number = dive.number,
    location = location?.toEntity(),
    start = dive.start,
    diveTime = dive.diveTime,
    maxDepthMeters = dive.maxDepthMeters,
    minTemperatureCelsius = dive.minTemperatureCelsius,
    depthProfile = depthProfile?.toEntity(),
    notes = dive.notes,
)

private fun DiveSpotDto.toEntity() = DiveSpot(
    id = id,
    name = name,
    coordinates = GeoCoordinates.fromNullable(latitude = latitude, longitude = longitude),
)

private fun DepthProfileDto.toEntity() = DepthProfile(
    id = id,
    samplingRate = samplingRate,
    depthCentimeters = depthCentimeters,
)

internal fun Dive.toDto() = DiveWithLocationAndProfile(
    dive = DiveDto(
        id = id,
        number = number,
        locationId = location?.id,
        start = start,
        diveTime = diveTime,
        maxDepthMeters = maxDepthMeters,
        minTemperatureCelsius = minTemperatureCelsius,
        notes = notes,
    ),
    location = location?.toDto(),
    depthProfile = depthProfile?.toDto(diveId = id),
)

private fun DiveSpot.toDto() = DiveSpotDto(
    id = id,
    name = name,
    latitude = coordinates?.latitude,
    longitude = coordinates?.longitude,
)

private fun DepthProfile.toDto(diveId: UUID) = DepthProfileDto(
    id = id,
    diveId = diveId,
    samplingRate = samplingRate,
    depthCentimeters = depthCentimeters,
)
