package cloud.mike.divelog.ui.home

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cloud.mike.divelog.data.dives.Dive
import cloud.mike.divelog.data.dives.DiveRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

@Immutable
data class HomeState(
    val query: String = "",
    val dives: List<Dive>? = null,
)

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel(
    diveRepo: DiveRepository,
) : ViewModel() {

    private val query = MutableStateFlow("")
    private val dives = query.flatMapLatest { diveRepo.fetchDives(it) }

    val uiState = combine(
        query,
        dives,
        ::HomeState,
    ).stateIn(viewModelScope, SharingStarted.Eagerly, HomeState())

    fun search(query: String) {
        this.query.update { query }
    }
}
