package cloud.mike.divelog.data.dives

import androidx.annotation.FloatRange
import java.util.UUID

data class DiveLocation(
    val id: UUID,
    val name: String,
    val coordinates: GeoCoordinates?,
) {
    companion object {
        val sample = DiveLocation(
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
) {
    companion object {
        fun fromNullable(latitude: Double?, longitude: Double?): GeoCoordinates? {
            if (latitude == null || longitude == null) return null
            return GeoCoordinates(latitude = latitude, longitude = longitude)
        }
    }
}
