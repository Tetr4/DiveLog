package cloud.mike.divelog.data.dives

import java.time.Instant
import java.util.UUID

data class Dive(
    val id: UUID,
    val location: String,
    val timestamp: Instant,
) {
    companion object {
        val sample = Dive(
            id = UUID.randomUUID(),
            location = "Cyprus",
            timestamp = Instant.now(),
        )
        val samples = (0..20).map {
            sample.copy(
                id = UUID.randomUUID(),
                location = "Dive: $it",
            )
        }
    }
}
