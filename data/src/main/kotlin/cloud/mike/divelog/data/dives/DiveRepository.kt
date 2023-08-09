package cloud.mike.divelog.data.dives

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import java.time.LocalDateTime
import java.util.UUID

class DiveRepository {

    // TODO use room database
    private var dives = MutableStateFlow<List<Dive>>(Dive.samples)

    val currentDiveNumber
        get() = dives.value.maxOfOrNull { it.number } ?: 0

    suspend fun fetchDives(query: String): Flow<List<Dive>> {
        delay(200)
        return dives.map { dives ->
            dives
                .filter { it.matches(query) }
                .sortedByDescending { it.number }
        }
    }

    suspend fun fetchDive(id: UUID): Dive {
        delay(1000)
        return dives.value.find { it.id == id } ?: error("Dive not found")
    }

    suspend fun addDives(newDives: List<Dive>) {
        delay(200)
        dives.update { it + newDives }
    }

    suspend fun updateDive(dive: Dive) {
        delay(200)
        dives.update { dives -> dives.filter { it.id == dive.id } + dive }
    }

    // TODO Duplicate checking will become more complex once it is possible to delete, edit or merge dives.
    fun containsDiveAt(timestamp: LocalDateTime) = dives.value.any { it.start == timestamp }
}

private fun Dive.matches(query: String): Boolean {
    return location?.name.orEmpty().contains(query, ignoreCase = true)
}
