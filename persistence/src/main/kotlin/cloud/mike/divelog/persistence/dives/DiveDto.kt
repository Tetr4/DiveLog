package cloud.mike.divelog.persistence.dives

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.LocalDateTime
import java.util.UUID
import kotlin.time.Duration

@Entity(
    tableName = "dives",
    foreignKeys = [
        ForeignKey(
            entity = DiveSpotDto::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("locationId"),
            onDelete = ForeignKey.SET_NULL,
        ),
    ],
)
data class DiveDto(
    @PrimaryKey val id: UUID,
    val number: Int,
    @ColumnInfo(index = true) val locationId: UUID?,
    val start: LocalDateTime,
    val diveTime: Duration,
    val maxDepthMeters: Float?,
    val minTemperatureCelsius: Float?,
    val notes: String?,
)
