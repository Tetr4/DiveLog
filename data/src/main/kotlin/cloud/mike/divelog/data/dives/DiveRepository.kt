package cloud.mike.divelog.data.dives

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import java.util.UUID

class DiveRepository {

    // TODO use room database
    private var dives = MutableStateFlow(Dive.samples)

    suspend fun fetchDives(query: String): Flow<List<Dive>> {
        delay(200)
        return dives.map { dives ->
            dives.filter { it.matches(query) }
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
}

private fun Dive.matches(query: String): Boolean {
    return location.contains(query, ignoreCase = true)
}
