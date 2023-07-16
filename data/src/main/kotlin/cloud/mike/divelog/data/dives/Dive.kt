package cloud.mike.divelog.data.dives

import java.time.Duration
import java.time.LocalDateTime
import java.util.UUID
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

data class Dive(
    val id: UUID,
    val number: Int,
    val location: String,
    val start: LocalDateTime,
    val diveTime: Duration,
    val maxDepthMeters: Float?,
    val minTemperatureCelsius: Float?,
    val diveProfile: DiveProfile? = null,
) {
    companion object {
        val sample = Dive(
            id = UUID.randomUUID(),
            number = 1,
            location = "Sha'ab Hamam, Egypt",
            start = LocalDateTime.now(),
            diveTime = DiveProfile.sample.samplingRate.multipliedBy(
                DiveProfile.sample.depthCentimeters.size.toLong(),
            ),
            maxDepthMeters = DiveProfile.sample.depthCentimeters.max() / 100f,
            minTemperatureCelsius = 20f,
            diveProfile = DiveProfile.sample,
        )
        val samples = List(20) { index ->
            sample.copy(
                id = UUID.randomUUID(),
                number = index + 1,
                location = "Sha'ab Hamam, Egypt",
            )
        }.reversed()
    }
}

class DiveProfile(
    val samplingRate: Duration,
    val depthCentimeters: IntArray,
) {
    companion object {
        private const val NUM_SAMPLE_POINTS = 30
        private const val SAMPLE_DEPTH_METERS = 25

        val sample = DiveProfile(
            samplingRate = Duration.ofMinutes(1),
            depthCentimeters = IntArray(NUM_SAMPLE_POINTS) {
                val progress = it / (NUM_SAMPLE_POINTS - 1.0)
                val normalized = sin(progress * PI) // 0 to 1
                val withJitter = normalized + 0.1 * (cos(progress * 7 * PI) + 1)
                (withJitter * SAMPLE_DEPTH_METERS * 100.0).roundToInt()
            },
        )
    }
}
