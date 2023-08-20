package cloud.mike.divelog.persistence.dives

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime
import java.util.UUID

@Dao
interface DivesDao {

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
    fun loadDivesPages(query: String): PagingSource<Int, DiveWithLocationAndProfile>

    @Transaction
    @Query("SELECT * FROM dives WHERE id=:id")
    fun loadDiveStream(id: UUID): Flow<DiveWithLocationAndProfile?>

    @Update
    suspend fun updateDive(dive: DiveDto)

    @Delete
    suspend fun deleteDive(id: DiveDto)

    @Query("SELECT MAX(number) FROM dives")
    suspend fun loadMaxDiveNumber(): Int?

    @Query("SELECT EXISTS(SELECT * FROM dives WHERE start = :timestamp)")
    suspend fun diveExists(timestamp: LocalDateTime): Boolean

    @Insert
    suspend fun insertDive(dive: DiveDto)

    @Insert
    suspend fun insertProfile(profile: DepthProfileDto)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocation(location: DiveSpotDto)

    @Transaction
    suspend fun insertDives(dives: List<DiveWithLocationAndProfile>) = dives.forEach {
        if (it.location != null) insertLocation(it.location)
        insertDive(it.dive)
        if (it.depthProfile != null) insertProfile(it.depthProfile)
    }
}
