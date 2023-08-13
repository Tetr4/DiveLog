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

    fun getDivePages(query: String): Flow<PagingData<Dive>> {
        val pager = Pager(
            config = PagingConfig(pageSize = PAGE_SIZE, enablePlaceholders = false),
            pagingSourceFactory = { divesDao.loadDivesPages(query) },
        )
        return pager.flow.map { diveDtos -> diveDtos.map { it.toEntity() } }
    }

    suspend fun deleteDive(id: UUID) = divesDao.deleteDive(id)
    suspend fun addDives(newDives: List<Dive>) = divesDao.insertDives(newDives.map { it.toDto() })
    suspend fun getCurrentDiveNumber(): Int = divesDao.loadMaxDiveNumber() ?: 0
    suspend fun containsDiveAt(timestamp: LocalDateTime): Boolean = divesDao.diveExists(timestamp)
}
