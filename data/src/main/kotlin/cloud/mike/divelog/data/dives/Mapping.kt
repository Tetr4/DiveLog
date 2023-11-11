package cloud.mike.divelog.data.dives

import cloud.mike.divelog.persistence.dives.DiveDto
import cloud.mike.divelog.persistence.dives.DiveLocationDto
import cloud.mike.divelog.persistence.dives.DiveProfileDto
import cloud.mike.divelog.persistence.dives.DiveWithLocationAndProfile
import java.util.UUID

internal fun DiveWithLocationAndProfile.toEntity() = Dive(
    id = dive.id,
    number = dive.number,
    startDate = dive.startDate,
    startTime = dive.startTime,
    duration = dive.duration,
    maxDepthMeters = dive.maxDepthMeters,
    minTemperatureCelsius = dive.minTemperatureCelsius,
    location = location?.toEntity(),
    profile = profile?.toEntity(),
    notes = dive.notes,
)

private fun DiveLocationDto.toEntity() = DiveLocation(
    id = id,
    name = name,
    coordinates = GeoCoordinates.fromNullable(latitude = latitude, longitude = longitude),
)

private fun DiveProfileDto.toEntity() = DiveProfile(
    id = id,
    samplingRate = samplingRate,
    depthCentimeters = depthCentimeters,
)

internal fun Dive.toDto() = DiveWithLocationAndProfile(
    dive = DiveDto(
        id = id,
        number = number,
        locationId = location?.id,
        startDate = startDate,
        startTime = startTime,
        duration = duration,
        maxDepthMeters = maxDepthMeters,
        minTemperatureCelsius = minTemperatureCelsius,
        notes = notes,
    ),
    location = location?.toDto(),
    profile = profile?.toDto(diveId = id),
)

private fun DiveLocation.toDto() = DiveLocationDto(
    id = id,
    name = name,
    latitude = coordinates?.latitude,
    longitude = coordinates?.longitude,
)

private fun DiveProfile.toDto(diveId: UUID) = DiveProfileDto(
    id = id,
    diveId = diveId,
    samplingRate = samplingRate,
    depthCentimeters = depthCentimeters,
)
