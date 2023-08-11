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

    fun getDiveStream(id: UUID): Flow<Dive?> = divesDao.loadDiveStream(id).map { it?.toEntity() }
    fun getDivesStream(query: String): Flow<List<Dive>> = divesDao.loadDivesStream()
        .distinctUntilChanged()
        .map { diveDtos ->
            diveDtos
                .map { it.toEntity() }
                .filter { it.matches(query) } // TODO do this in SQL
        }

    suspend fun deleteDive(id: UUID) = divesDao.deleteDive(id)
    suspend fun addDives(newDives: List<Dive>) = divesDao.insertDives(newDives.map { it.toDto() })
    suspend fun getCurrentDiveNumber(): Int = divesDao.loadMaxDiveNumber() ?: 0
    suspend fun containsDiveAt(timestamp: LocalDateTime): Boolean = divesDao.diveExists(timestamp)
}

private fun Dive.matches(query: String) = location?.name.orEmpty().contains(query, ignoreCase = true)
