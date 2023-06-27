package cloud.mike.divelog.data.dives

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.UUID

class DiveRepository {
    // TODO implement me
    val dives = MutableStateFlow(Dive.samples)

    suspend fun fetchDive(id: UUID): Dive {
        delay(2000)
        return Dive.sample.copy(id = id)
    }
}
