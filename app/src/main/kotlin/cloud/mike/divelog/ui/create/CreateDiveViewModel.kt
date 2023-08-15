package cloud.mike.divelog.ui.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cloud.mike.divelog.data.dives.Dive
import cloud.mike.divelog.data.dives.DiveRepository
import cloud.mike.divelog.localization.errors.ErrorMessage
import cloud.mike.divelog.localization.errors.ErrorService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.UUID
import kotlin.time.Duration

data class FormData(
    val start: LocalDateTime,
    val diveTime: Duration,
)

sealed interface SaveState {
    data object Idle : SaveState
    data object Saving : SaveState
    data class Success(val createdDive: Dive) : SaveState
    data class Error(val message: ErrorMessage) : SaveState
}

data class CreateDiveState(
    val saveState: SaveState = SaveState.Idle,
)

class CreateDiveViewModel(
    private val errorService: ErrorService,
    private val diveRepo: DiveRepository,
) : ViewModel() {

    private val saveState = MutableStateFlow<SaveState>(SaveState.Idle)

    val uiState = saveState.map(::CreateDiveState)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CreateDiveState())

    fun save(data: FormData) {
        viewModelScope.launch {
            try {
                saveState.update { SaveState.Saving }
                val dive = Dive(
                    id = UUID.randomUUID(),
                    start = data.start,
                    diveTime = data.diveTime,
                    number = diveRepo.getCurrentDiveNumber() + 1,
                    location = null,
                    maxDepthMeters = null,
                    minTemperatureCelsius = null,
                    depthProfile = null,
                    notes = null,
                )
                diveRepo.addDive(dive)
                saveState.update { SaveState.Success(dive) }
            } catch (e: Exception) {
                saveState.update { SaveState.Error(errorService.createMessage(e)) }
            }
        }
    }
}
