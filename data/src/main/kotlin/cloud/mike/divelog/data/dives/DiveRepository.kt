package cloud.mike.divelog.data.dives

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import cloud.mike.divelog.persistence.dives.DivesDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import java.util.UUID

private const val PAGE_SIZE = 50

class DiveRepository(
    private val divesDao: DivesDao,
) {

    fun getDiveStream(id: UUID): Flow<Dive?> = divesDao.loadDiveStream(id).map { it?.toEntity() }

    fun getDivesPages(query: String): Flow<PagingData<Dive>> {
        val pager = Pager(
            config = PagingConfig(pageSize = PAGE_SIZE, enablePlaceholders = false),
            pagingSourceFactory = { divesDao.loadDivesPages(query) },
        )
        return pager.flow.map { diveDtos -> diveDtos.map { it.toEntity() } }
    }

    suspend fun deleteDive(dive: Dive) = divesDao.deleteDive(dive.toDto().dive)
    suspend fun addDive(dive: Dive) = addDives(listOf(dive))
    suspend fun addDives(dives: List<Dive>) = divesDao.insertDives(dives.map { it.toDto() })
    suspend fun updateDive(dive: Dive) = divesDao.updateDive(dive.toDto().dive)

    suspend fun getCurrentDiveNumber(): Int = divesDao.loadMaxDiveNumber() ?: 0
    suspend fun containsDiveAt(timestamp: LocalDateTime): Boolean = divesDao.diveExists(timestamp)
}
