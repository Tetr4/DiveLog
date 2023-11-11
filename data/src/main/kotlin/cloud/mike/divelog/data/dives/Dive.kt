package cloud.mike.divelog.data.dives

import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID
import kotlin.time.Duration

data class Dive(
    val id: UUID,
    val number: Int,
    val startDate: LocalDate,
    val startTime: LocalTime?,
    val duration: Duration,
    val maxDepthMeters: Float?,
    val minTemperatureCelsius: Float?,
    val location: DiveLocation?,
    val profile: DiveProfile?,
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
            startDate = LocalDate.now(),
            startTime = LocalTime.now(),
            duration = DiveProfile.sample.samplingRate.times(DiveProfile.sample.depthCentimeters.size),
            maxDepthMeters = DiveProfile.sample.depthCentimeters.max() / 100f,
            minTemperatureCelsius = 20f,
            location = DiveLocation.sample,
            profile = DiveProfile.sample,
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
