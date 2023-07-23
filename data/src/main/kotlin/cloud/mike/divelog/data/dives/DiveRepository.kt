package cloud.mike.divelog.data.dives

import cloud.mike.divelog.data.importer.frames.CompactHeaderFrame
import cloud.mike.divelog.data.importer.frames.DiveProfileFrame
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import java.util.UUID

class DiveRepository {

    // TODO use room database
    private var dives = MutableStateFlow<List<Dive>>(emptyList())

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

    suspend fun addDive(dive: Dive) {
        delay(200)
        dives.update { it + dive }
    }

    suspend fun updateDive(dive: Dive) {
        delay(200)
        dives.update { dives -> dives.filter { it.id == dive.id } + dive }
    }

    fun importFromDiveComputer(profiles: List<DiveProfileFrame>) {
        var maxDiveNumber = dives.value.maxOfOrNull { it.number } ?: 0
        val newDives = profiles.map { it.toDive(++maxDiveNumber) }
        dives.update { it + newDives }
    }

    fun contains(header: CompactHeaderFrame) = dives.value.any { it.start == header.timestamp }
}

private fun Dive.matches(query: String): Boolean {
    return location.contains(query, ignoreCase = true)
}
