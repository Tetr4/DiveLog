package cloud.mike.divelog.data.dives

import java.time.LocalDateTime
import java.util.UUID
import kotlin.time.Duration

data class Dive(
    val id: UUID,
    val number: Int,
    val location: DiveSpot?,
    val start: LocalDateTime,
    val diveTime: Duration,
    val maxDepthMeters: Float?,
    val minTemperatureCelsius: Float?,
    val depthProfile: DepthProfile?,
    val notes: String?, // TODO DiveNotes class for observations/gear?
    // TODO more fields:
    //  - tags
    //  - buddy signature?
    //  - mark as favorite?
) {
    companion object {
        val sample = Dive(
            id = UUID.randomUUID(),
            number = 1,
            location = DiveSpot.sample,
            start = LocalDateTime.now(),
            diveTime = DepthProfile.sample.samplingRate.times(DepthProfile.sample.depthCentimeters.size),
            maxDepthMeters = DepthProfile.sample.depthCentimeters.max() / 100f,
            minTemperatureCelsius = 20f,
            depthProfile = DepthProfile.sample,
            notes = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt" +
                " ut labore et dolore magna aliquyam erat, sed diam voluptua.",
        )
        val samples = List(20) { index ->
            sample.copy(
                id = UUID.randomUUID(),
                number = index + 1,
            )
        }.reversed()
    }
}
