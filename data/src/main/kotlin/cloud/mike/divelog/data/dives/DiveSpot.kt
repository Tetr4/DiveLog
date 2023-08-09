package cloud.mike.divelog.data.dives

import androidx.annotation.FloatRange
import java.util.UUID

data class DiveSpot(
    val id: UUID,
    val name: String,
    val coordinates: GeoCoordinates?,
) {
    companion object {
        val sample = DiveSpot(
            id = UUID.randomUUID(),
            name = "Sha'ab Hamam, Egypt",
            coordinates = GeoCoordinates(
                latitude = 24.262363202339,
                longitude = 35.51645954667073,
            ),
        )
    }
}

data class GeoCoordinates(
    @FloatRange(from = -90.0, to = 90.0) val latitude: Double,
    @FloatRange(from = -180.0, to = 180.0) val longitude: Double,
)
