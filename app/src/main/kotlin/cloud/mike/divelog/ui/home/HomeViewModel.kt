package cloud.mike.divelog.ui.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cloud.mike.divelog.data.dives.Dive
import cloud.mike.divelog.data.dives.DiveRepository
import kotlinx.coroutines.launch

class HomeViewModel(
    diveRepo: DiveRepository,
) : ViewModel() {

    var dives: List<Dive>? by mutableStateOf(null)

    init {
        viewModelScope.launch {
            diveRepo.dives.collect { dives = it }
        }
    }
}
