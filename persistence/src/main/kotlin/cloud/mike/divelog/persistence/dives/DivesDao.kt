package cloud.mike.divelog.persistence.dives

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime
import java.util.UUID

@Dao
interface DivesDao {

    @Transaction
    @Query("SELECT * FROM dives ORDER BY number DESC")
    fun loadDivesPages(): PagingSource<Int, DiveWithLocationAndProfile>

    @Transaction
    @Query("SELECT * FROM dives WHERE id=:id")
    fun loadDiveStream(id: UUID): Flow<DiveWithLocationAndProfile?>

    @Query("DELETE FROM dives WHERE id = :id")
    suspend fun deleteDive(id: UUID)

    @Query("SELECT MAX(number) FROM dives")
    suspend fun loadMaxDiveNumber(): Int?

    @Query("SELECT EXISTS(SELECT * FROM dives WHERE start = :timestamp)")
    suspend fun diveExists(timestamp: LocalDateTime): Boolean

    @Query("DELETE FROM dives")
    suspend fun clearDives()

    @Transaction
    suspend fun insertDives(dives: List<DiveWithLocationAndProfile>) = dives.forEach {
        if (it.location != null) insertLocation(it.location)
        insertDive(it.dive)
        if (it.depthProfile != null) insertProfile(it.depthProfile)
    }

    @Insert
    suspend fun insertDive(dive: DiveDto)

    @Insert
    suspend fun insertProfile(profile: DepthProfileDto)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocation(location: DiveSpotDto)
}
