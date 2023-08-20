package cloud.mike.divelog.ui.edit

import androidx.compose.runtime.Immutable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cloud.mike.divelog.data.dives.Dive
import cloud.mike.divelog.data.dives.DiveRepository
import cloud.mike.divelog.data.logging.logError
import cloud.mike.divelog.localization.errors.ErrorMessage
import cloud.mike.divelog.localization.errors.ErrorService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.UUID
import kotlin.time.Duration

data class FormData(
    val start: LocalDateTime,
    val diveTime: Duration,
    val notes: String?,
)

@Immutable
sealed interface DiveState {
    val dive: Dive?
        get() = null

    data object Loading : DiveState
    data class Error(val message: ErrorMessage) : DiveState
    data object Create : DiveState
    data class Update(override val dive: Dive) : DiveState
}

@Immutable
sealed interface SaveState {
    data object Idle : SaveState
    data object Saving : SaveState
    data class Error(val message: ErrorMessage) : SaveState
    data class Created(val dive: Dive) : SaveState
    data object Updated : SaveState
}

@Immutable
data class EditState(
    val diveState: DiveState = DiveState.Create,
    val saveState: SaveState = SaveState.Idle,
)

class EditViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val diveRepo: DiveRepository,
    private val errorService: ErrorService,
) : ViewModel() {

    private val diveState = MutableStateFlow<DiveState>(DiveState.Create)
    private val saveState = MutableStateFlow<SaveState>(SaveState.Idle)

    val uiState = combine(
        diveState,
        saveState,
        ::EditState,
    ).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), EditState())

    init {
        fetchDive()
    }

    fun fetchDive() {
        // If diveId is given, we are editing an existing dive. Otherwise we are creating a new dive.
        savedStateHandle.diveId?.let(::fetchDive)
    }

    private fun fetchDive(id: UUID) {
        viewModelScope.launch {
            try {
                diveState.update { DiveState.Loading }
                val dive = diveRepo.getDiveStream(id).first() ?: error("Dive not found")
                diveState.update { DiveState.Update(dive) }
            } catch (e: Exception) {
                logError(e)
                diveState.update { DiveState.Error(errorService.createMessage(e)) }
            }
        }
    }

    fun save(data: FormData) {
        viewModelScope.launch {
            try {
                saveState.update { SaveState.Saving }
                when (val state = diveState.value) {
                    is DiveState.Loading, is DiveState.Error -> error("Dive not yet loaded")
                    is DiveState.Create -> {
                        val dive = createDive(data)
                        saveState.update { SaveState.Created(dive) }
                    }
                    is DiveState.Update -> {
                        updateDive(state.dive, data)
                        saveState.update { SaveState.Updated }
                    }
                }
            } catch (e: Exception) {
                logError(e)
                saveState.update { SaveState.Error(errorService.createMessage(e)) }
            }
        }
    }

    private suspend fun createDive(data: FormData): Dive {
        val new = Dive(
            id = UUID.randomUUID(),
            start = data.start,
            diveTime = data.diveTime,
            number = diveRepo.getCurrentDiveNumber() + 1,
            location = null,
            maxDepthMeters = null,
            minTemperatureCelsius = null,
            depthProfile = null,
            notes = data.notes,
        )
        diveRepo.addDive(new)
        return new
    }

    private suspend fun updateDive(old: Dive, data: FormData) {
        val new = old.copy(
            start = data.start,
            diveTime = data.diveTime,
            notes = data.notes,
        )
        diveRepo.updateDive(new)
    }
}
