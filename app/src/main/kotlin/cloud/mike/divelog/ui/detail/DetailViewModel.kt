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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Immutable
sealed interface DetailState {
    val dive: Dive?
        get() = null

    data object Loading : DetailState
    data class Error(val message: ErrorMessage) : DetailState
    data class Success(override val dive: Dive) : DetailState
}

class DetailViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val diveRepo: DiveRepository,
    private val errorService: ErrorService,
) : ViewModel() {

    private val _uiState = MutableStateFlow<DetailState>(DetailState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        fetchDive()
    }

    fun fetchDive() {
        viewModelScope.launch {
            try {
                _uiState.update { DetailState.Loading }
                val dive = diveRepo.fetchDive(savedStateHandle.diveId)
                _uiState.update { DetailState.Success(dive) }
            } catch (e: Exception) {
                logError(e)
                _uiState.update { DetailState.Error(errorService.createMessage(e)) }
            }
        }
    }
}
