package cloud.mike.divelog.persistence.dives

import androidx.annotation.FloatRange
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "locations")
data class DiveLocationDto(
    @PrimaryKey val id: UUID,
    val name: String,
    @FloatRange(from = -90.0, to = 90.0) val latitude: Double?,
    @FloatRange(from = -180.0, to = 180.0) val longitude: Double?,
)
