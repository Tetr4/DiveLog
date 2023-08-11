package cloud.mike.divelog.ui.detail

import androidx.compose.runtime.Immutable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cloud.mike.divelog.data.dives.Dive
import cloud.mike.divelog.data.dives.DiveRepository
import cloud.mike.divelog.data.logging.logError
import cloud.mike.divelog.localization.errors.ErrorMessage
import cloud.mike.divelog.localization.errors.ErrorService
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Immutable
sealed interface DeleteState {
    data object Idle : DeleteState
    data object Loading : DeleteState
    data class Error(val message: ErrorMessage) : DeleteState
    data object Success : DeleteState
}

@Immutable
sealed interface DiveState {
    val dive: Dive?
        get() = null

    data object Loading : DiveState
    data class Error(val message: ErrorMessage) : DiveState
    data object Empty : DiveState
    data class Content(override val dive: Dive) : DiveState
}

@Immutable
data class DetailState(
    val diveState: DiveState = DiveState.Empty,
    val deleteState: DeleteState = DeleteState.Idle,
)

class DetailViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val diveRepo: DiveRepository,
    private val errorService: ErrorService,
) : ViewModel() {

    private var observeJob: Job? = null
    private val diveState = MutableStateFlow<DiveState>(DiveState.Empty)
    private val deleteState = MutableStateFlow<DeleteState>(DeleteState.Idle)

    val uiState = combine(
        diveState,
        deleteState,
        ::DetailState,
    ).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DetailState())

    init {
        fetchDive()
    }

    fun fetchDive() {
        observeJob?.cancel()
        observeJob = viewModelScope.launch {
            diveRepo.getDiveStream(savedStateHandle.diveId)
                .onStart {
                    diveState.update { DiveState.Loading }
                }
                .catch { e ->
                    logError(e)
                    diveState.update { DiveState.Error(errorService.createMessage(e)) }
                }
                .collect { dive ->
                    diveState.update {
                        if (dive == null) DiveState.Empty else DiveState.Content(dive)
                    }
                }
        }
    }

    fun deleteDive() {
        viewModelScope.launch {
            try {
                deleteState.update { DeleteState.Loading }
                diveRepo.deleteDive(savedStateHandle.diveId)
                deleteState.update { DeleteState.Success }
            } catch (e: Exception) {
                logError(e)
                deleteState.update { DeleteState.Error(errorService.createMessage(e)) }
            }
        }
    }

    fun dismissDeleteError() {
        deleteState.update { DeleteState.Idle }
    }
}
