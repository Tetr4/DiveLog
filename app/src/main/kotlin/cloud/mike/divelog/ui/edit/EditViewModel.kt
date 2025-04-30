package cloud.mike.divelog.ui.edit

import androidx.compose.runtime.Immutable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cloud.mike.divelog.data.dives.Dive
import cloud.mike.divelog.data.dives.DiveLocation
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
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID
import kotlin.time.Duration

data class DiveData(
    val number: Int,
    val startDate: LocalDate,
    val startTime: LocalTime?,
    val duration: Duration,
    val location: String?, // TODO allow selecting GPS coordinates
    val buddy: String?,
    val notes: String?,
    val maxDepthMeters: Float?,
)

@Immutable
sealed interface DiveState {
    data object Loading : DiveState
    data class Error(val message: ErrorMessage) : DiveState
    data class Create(val nextDiveNumber: Int) : DiveState
    data class Update(val dive: Dive) : DiveState
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
    val diveState: DiveState = DiveState.Loading,
    val saveState: SaveState = SaveState.Idle,
)

class EditViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val diveRepo: DiveRepository,
    private val errorService: ErrorService,
) : ViewModel() {

    private val diveState = MutableStateFlow<DiveState>(DiveState.Loading)
    private val saveState = MutableStateFlow<SaveState>(SaveState.Idle)

    val uiState = combine(
        diveState,
        saveState,
        ::EditState,
    ).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), EditState())

    init {
        fetchDiveData()
    }

    fun fetchDiveData() {
        viewModelScope.launch {
            try {
                diveState.update { DiveState.Loading }
                // If diveId is given, we are editing an existing dive. Otherwise we are creating a new dive.
                val diveId = savedStateHandle.diveId
                if (diveId != null) {
                    val dive = diveRepo.getDiveStream(diveId).first() ?: error("Dive not found")
                    diveState.update { DiveState.Update(dive) }
                } else {
                    val nextDiveNumber = diveRepo.getNextDiveNumber()
                    diveState.update { DiveState.Create(nextDiveNumber) }
                }
            } catch (e: Exception) {
                logError(e)
                diveState.update { DiveState.Error(errorService.createMessage(e)) }
            }
        }
    }

    fun save(data: DiveData) {
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

    private suspend fun createDive(data: DiveData): Dive {
        val new = Dive(
            id = UUID.randomUUID(),
            startDate = data.startDate,
            startTime = data.startTime,
            duration = data.duration,
            number = data.number,
            location = data.location?.let {
                DiveLocation(
                    id = UUID.randomUUID(),
                    name = data.location,
                    coordinates = null,
                )
            },
            maxDepthMeters = data.maxDepthMeters,
            minTemperatureCelsius = null,
            profile = null,
            buddy = data.buddy,
            notes = data.notes,
        )
        diveRepo.addDive(new)
        return new
    }

    private suspend fun updateDive(old: Dive, data: DiveData) {
        val new = old.copy(
            number = data.number,
            startDate = data.startDate,
            startTime = data.startTime,
            duration = data.duration,
            location = data.location?.let {
                old.location?.copy(
                    name = data.location,
                ) ?: DiveLocation(
                    id = UUID.randomUUID(),
                    name = data.location,
                    coordinates = null,
                )
            },
            maxDepthMeters = data.maxDepthMeters,
            buddy = data.buddy,
            notes = data.notes,
        )
        diveRepo.updateDive(new)
    }
}
