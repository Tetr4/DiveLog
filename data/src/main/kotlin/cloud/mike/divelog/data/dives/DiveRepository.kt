package cloud.mike.divelog.data.dives

import kotlinx.coroutines.flow.MutableStateFlow

class DiveRepository {
    // TODO implement me
    val dives = MutableStateFlow(Dive.samples)
}
