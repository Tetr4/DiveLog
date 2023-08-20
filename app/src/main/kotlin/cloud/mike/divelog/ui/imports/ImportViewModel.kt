package cloud.mike.divelog.ui.imports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cloud.mike.divelog.data.dives.Dive
import cloud.mike.divelog.data.dives.DiveRepository
import cloud.mike.divelog.data.importer.ImportConnectionState
import cloud.mike.divelog.data.importer.Importer
import cloud.mike.divelog.data.logging.logError
import cloud.mike.divelog.localization.errors.ErrorMessage
import cloud.mike.divelog.localization.errors.ErrorService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface TransferState {
    data object Idle : TransferState
    data class Transfering(val progress: Float?) : TransferState
    data class Error(val message: ErrorMessage) : TransferState
    data class Success(val importedDives: List<Dive>) : TransferState
}

data class ImportState(
    val connectionState: ImportConnectionState,
    val transferState: TransferState = TransferState.Idle,
    val connectionError: ErrorMessage? = null,
)

class ImportViewModel(
    private val importer: Importer,
    private val errorService: ErrorService,
    private val diveRepo: DiveRepository,
) : ViewModel() {

    private val transferState = MutableStateFlow<TransferState>(TransferState.Idle)
    private val errorFlow = importer.errorFlow
        .map<Exception, ErrorMessage?> { errorService.createMessage(it) }
        .onStart { emit(null) }

    val uiState = combine(
        importer.connectionStateFlow,
        transferState,
        errorFlow,
        ::ImportState,
    ).stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        ImportState(connectionState = importer.connectionState),
    )

    fun connect() = importer.connect()

    fun disconnect() = importer.disconnect()

    fun startTransfer() {
        val connection = importer.connection ?: error("Not connected")
        viewModelScope.launch {
            try {
                transferState.update { TransferState.Transfering(progress = null) }
                val dives = connection.fetchDives(
                    initialDiveNumber = diveRepo.getNextDiveNumber(),
                    isAlreadyImported = { timestamp -> diveRepo.containsDiveAt(timestamp) },
                    onProgress = { progress -> transferState.update { TransferState.Transfering(progress) } },
                )
                diveRepo.addDives(dives)
                transferState.update { TransferState.Success(dives) }
            } catch (e: Exception) {
                logError(e)
                transferState.update { TransferState.Error(errorService.createMessage(e)) }
            }
        }
    }
}
