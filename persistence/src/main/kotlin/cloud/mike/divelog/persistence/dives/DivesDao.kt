package cloud.mike.divelog.persistence.dives

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
    fun getDivesFlow(): Flow<List<DiveWithLocationAndProfile>>

    @Transaction
    @Query("SELECT * FROM dives WHERE id=:id LIMIT 1")
    suspend fun findDive(id: UUID): DiveWithLocationAndProfile

    @Query("SELECT MAX(number) FROM dives")
    suspend fun getMaxDiveNumber(): Int?

    @Query("SELECT EXISTS(SELECT * FROM dives WHERE start = :timestamp)")
    suspend fun diveExists(timestamp: LocalDateTime): Boolean

    @Query("DELETE FROM dives")
    suspend fun clearDives()

    @Transaction
    suspend fun insertAll(dives: List<DiveWithLocationAndProfile>) = dives.forEach {
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
