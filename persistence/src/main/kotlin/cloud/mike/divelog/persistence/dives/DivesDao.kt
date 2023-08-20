package cloud.mike.divelog.persistence.dives

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime
import java.util.UUID

@Dao
interface DivesDao {

    @Transaction
    @Query("SELECT * FROM dives WHERE id=:id")
    fun getDiveStream(id: UUID): Flow<DiveWithLocationAndProfile?>

    @Transaction
    @Query(
        """
            SELECT * FROM dives
            LEFT JOIN diveSpots on dives.locationId = diveSpots.id
            WHERE
                diveSpots.name LIKE '%' || :query || '%'
                OR notes LIKE '%' || :query || '%'
                OR STRFTIME('%d.%m.%Y', start) LIKE '%' || :query || '%'
            ORDER BY start DESC
        """,
    )
    fun getDivesPages(query: String): PagingSource<Int, DiveWithLocationAndProfile>

    @Query("SELECT EXISTS(SELECT * FROM dives WHERE start = :timestamp)")
    suspend fun diveExists(timestamp: LocalDateTime): Boolean

    @Query("SELECT MAX(number) FROM dives")
    suspend fun getMaxDiveNumber(): Int?

    @Delete
    suspend fun deleteDive(dive: DiveDto)

    @Upsert
    suspend fun upsertDiveHeader(dive: DiveDto)

    @Upsert
    suspend fun upsertProfile(profile: DepthProfileDto)

    @Upsert
    suspend fun upsertLocation(location: DiveSpotDto)

    @Transaction
    suspend fun upsertDives(dives: List<DiveWithLocationAndProfile>) = dives.forEach { upsertDive(it) }

    @Transaction
    suspend fun upsertDive(data: DiveWithLocationAndProfile) {
        if (data.location != null) upsertLocation(data.location)
        upsertDiveHeader(data.dive)
        if (data.depthProfile != null) upsertProfile(data.depthProfile)
    }
}
