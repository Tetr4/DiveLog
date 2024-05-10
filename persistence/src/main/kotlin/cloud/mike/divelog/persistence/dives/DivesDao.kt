package cloud.mike.divelog.persistence.dives

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

@Dao
interface DivesDao {

    @Transaction
    @Query("SELECT * FROM dives WHERE id=:id")
    fun getDiveStream(id: UUID): Flow<DiveWithLocationAndProfile?>

    @Transaction
    @Query(
        """
            SELECT dives.* FROM dives
            LEFT JOIN locations on dives.locationId = locations.id
            WHERE
                locations.name LIKE '%' || :query || '%'
                OR notes LIKE '%' || :query || '%'
                OR buddy LIKE '%' || :query || '%'
                OR STRFTIME('%d.%m.%Y', startDate) LIKE '%' || :query || '%'
            ORDER BY startDate DESC, startTime DESC, number DESC
        """,
    )
    fun getDivesPages(query: String): PagingSource<Int, DiveWithLocationAndProfile>

    @Query("SELECT EXISTS(SELECT * FROM dives WHERE startDate = :startDate AND startTime = :startTime)")
    suspend fun diveExists(startDate: LocalDate, startTime: LocalTime): Boolean

    @Query("SELECT MAX(number) FROM dives")
    suspend fun getMaxDiveNumber(): Int?

    @Delete
    suspend fun deleteDive(dive: DiveDto)

    @Upsert
    suspend fun upsertDiveHeader(dive: DiveDto)

    @Upsert
    suspend fun upsertProfile(profile: DiveProfileDto)

    @Upsert
    suspend fun upsertLocation(location: DiveLocationDto)

    @Transaction
    suspend fun upsertDives(dives: List<DiveWithLocationAndProfile>) = dives.forEach { upsertDive(it) }

    @Transaction
    suspend fun upsertDive(data: DiveWithLocationAndProfile) {
        if (data.location != null) upsertLocation(data.location)
        upsertDiveHeader(data.dive)
        if (data.profile != null) upsertProfile(data.profile)
    }
}
