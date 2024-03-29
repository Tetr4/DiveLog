package cloud.mike.divelog.data.dives

import java.util.UUID
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

data class DiveProfile(
    val id: UUID,
    val samplingRate: Duration,
    val depthCentimeters: IntArray,
    // Important: Regenerate equals and hashCode, when adding parameters.
) {
    companion object {
        private const val NUM_SAMPLE_POINTS = 30
        private const val SAMPLE_DEPTH_METERS = 25

        val sample = DiveProfile(
            id = UUID.randomUUID(),
            samplingRate = 1.minutes,
            depthCentimeters = IntArray(NUM_SAMPLE_POINTS) {
                val progress = it / (NUM_SAMPLE_POINTS - 1.0)
                val normalized = sin(progress * PI) // 0 to 1
                val withJitter = normalized + 0.1 * (cos(progress * 7 * PI) + 1)
                (withJitter * SAMPLE_DEPTH_METERS * 100.0).roundToInt()
            },
        )
    }

    /** Generated */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as DiveProfile
        if (id != other.id) return false
        if (samplingRate != other.samplingRate) return false
        if (!depthCentimeters.contentEquals(other.depthCentimeters)) return false
        return true
    }

    /** Generated */
    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + samplingRate.hashCode()
        result = 31 * result + depthCentimeters.contentHashCode()
        return result
    }
}