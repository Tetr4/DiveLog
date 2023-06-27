package cloud.mike.divelog.ui.home

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cloud.mike.divelog.data.dives.Dive
import cloud.mike.divelog.data.dives.DiveRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@Immutable
data class HomeState(
    val dives: List<Dive>? = null,
)

class HomeViewModel(
    diveRepo: DiveRepository,
) : ViewModel() {

    val uiState: StateFlow<HomeState> = diveRepo.dives.map { HomeState(it) }
        .stateIn(viewModelScope, SharingStarted.Lazily, HomeState(diveRepo.dives.value))
}
