package cloud.mike.divelog.data.dives

import kotlinx.coroutines.flow.MutableStateFlow

class DiveRepository {
    // TODO implement me
    val dives = MutableStateFlow(Dive.samples)

    fun fetchDive(id: String) = Dive.sample
}
