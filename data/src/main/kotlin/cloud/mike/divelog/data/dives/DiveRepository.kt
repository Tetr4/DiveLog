package cloud.mike.divelog.data.dives

import cloud.mike.divelog.persistence.dives.DivesDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import java.util.UUID

class DiveRepository(
    private val divesDao: DivesDao,
) {

    fun fetchDives(query: String): Flow<List<Dive>> = divesDao.getDivesFlow()
        .distinctUntilChanged()
        .map { diveDtos ->
            diveDtos
                .map { it.toEntity() }
                .filter { it.matches(query) } // TODO do this in SQL
        }

    suspend fun fetchDive(id: UUID): Dive = divesDao.findDive(id).toEntity()
    suspend fun addDives(newDives: List<Dive>) = divesDao.insertAll(newDives.map { it.toDto() })
    suspend fun getCurrentDiveNumber(): Int = divesDao.getMaxDiveNumber() ?: 0
    suspend fun containsDiveAt(timestamp: LocalDateTime): Boolean = divesDao.diveExists(timestamp)
}

private fun Dive.matches(query: String) = location?.name.orEmpty().contains(query, ignoreCase = true)
