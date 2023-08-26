package cloud.mike.divelog.data.importer.shearwater

import cloud.mike.divelog.data.dives.DepthProfile
import cloud.mike.divelog.data.dives.Dive
import cloud.mike.divelog.data.importer.shearwater.frames.DiveData
import java.util.UUID
import kotlin.math.roundToInt

internal fun DiveData.toDive(number: Int) = Dive(
    id = UUID.randomUUID(),
    number = number,
    location = null,
    start = timestamp,
    diveTime = diveTime,
    maxDepthMeters = maxDepthMeters,
    minTemperatureCelsius = null,
    depthProfile = DepthProfile(
        id = UUID.randomUUID(),
        samplingRate = samplingRate,
        depthCentimeters = samples.map { (it.depthMeters * 100).roundToInt() }.toIntArray(),
    ),
    notes = null,
)