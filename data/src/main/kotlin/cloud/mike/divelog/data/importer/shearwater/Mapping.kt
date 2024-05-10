package cloud.mike.divelog.data.importer.shearwater

import cloud.mike.divelog.data.dives.Dive
import cloud.mike.divelog.data.dives.DiveProfile
import cloud.mike.divelog.data.importer.shearwater.frames.DiveData
import java.util.UUID
import kotlin.math.roundToInt

internal fun DiveData.toDive(number: Int) = Dive(
    id = UUID.randomUUID(),
    number = number,
    startDate = timestamp.toLocalDate(),
    startTime = timestamp.toLocalTime(),
    duration = duration,
    maxDepthMeters = maxDepthMeters,
    minTemperatureCelsius = null,
    location = null,
    profile = DiveProfile(
        id = UUID.randomUUID(),
        samplingRate = samplingRate,
        depthCentimeters = samples.map { (it.depthMeters * 100).roundToInt() }.toIntArray(),
    ),
    buddy = null,
    notes = null,
)