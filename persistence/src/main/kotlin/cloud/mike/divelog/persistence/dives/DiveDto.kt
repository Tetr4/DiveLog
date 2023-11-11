package cloud.mike.divelog.persistence.dives

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID
import kotlin.time.Duration

@Entity(
    tableName = "dives",
    foreignKeys = [
        ForeignKey(
            entity = DiveLocationDto::class,
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
    val startDate: LocalDate,
    val startTime: LocalTime?,
    val duration: Duration,
    val maxDepthMeters: Float?,
    val minTemperatureCelsius: Float?,
    val notes: String?,
)
