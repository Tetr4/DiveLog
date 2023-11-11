package cloud.mike.divelog.persistence.dives

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.UUID
import kotlin.time.Duration

@Entity(
    tableName = "profiles",
    foreignKeys = [
        ForeignKey(
            entity = DiveDto::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("diveId"),
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
data class DiveProfileDto(
    @PrimaryKey val id: UUID,
    @ColumnInfo(index = true) val diveId: UUID,
    val samplingRate: Duration,
    val depthCentimeters: IntArray,
    // Important: Regenerate equals and hashCode, when adding parameters.
) {

    /** Generated */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as DiveProfileDto
        if (id != other.id) return false
        if (diveId != other.diveId) return false
        if (samplingRate != other.samplingRate) return false
        if (!depthCentimeters.contentEquals(other.depthCentimeters)) return false
        return true
    }

    /** Generated */
    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + diveId.hashCode()
        result = 31 * result + samplingRate.hashCode()
        result = 31 * result + depthCentimeters.contentHashCode()
        return result
    }
}