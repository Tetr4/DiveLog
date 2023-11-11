package cloud.mike.divelog.persistence.dives

import androidx.room.Embedded
import androidx.room.Relation

data class DiveWithLocationAndProfile(
    @Embedded val dive: DiveDto,
    @Relation(parentColumn = "locationId", entityColumn = "id") val location: DiveLocationDto?,
    @Relation(parentColumn = "id", entityColumn = "diveId") val profile: DiveProfileDto?,
)
