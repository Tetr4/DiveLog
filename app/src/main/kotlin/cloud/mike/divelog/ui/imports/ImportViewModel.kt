package cloud.mike.divelog.ui.imports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cloud.mike.divelog.data.dives.DiveRepository
import cloud.mike.divelog.data.importer.DeviceConnectionState
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
    object Idle : TransferState
    data class Transfering(val progress: Float?) : TransferState
    data class Error(val message: ErrorMessage) : TransferState
    object Success : TransferState
}

data class ImportState(
    val deviceConnectionState: DeviceConnectionState,
    val transferState: TransferState = TransferState.Idle,
    val connectionError: ErrorMessage? = null,
)

class ImportViewModel(
    private val importer: Importer,
    private val errorService: ErrorService,
    private val diveRepo: DiveRepository,
) : ViewModel() {

    private val transferState = MutableStateFlow<TransferState>(TransferState.Idle)
    private val errorFlow = importer.errorFlow.map<Exception, ErrorMessage?> { errorService.createMessage(it) }

    val uiState = combine(
        importer.stateFlow,
        transferState,
        errorFlow.onStart { emit(null) },
        ::ImportState,
    ).stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        ImportState(deviceConnectionState = importer.state),
    )

    fun connect() = importer.connect()

    fun disconnect() = importer.disconnect()

    fun startTransfer() {
        viewModelScope.launch {
            try {
                transferState.update { TransferState.Transfering(progress = null) }
                val profiles = importer.downloadProfiles(
                    filterHeaders = { !diveRepo.contains(it) },
                    onProgress = { progress -> transferState.update { TransferState.Transfering(progress) } },
                )
                diveRepo.importFromDiveComputer(profiles)
                transferState.update { TransferState.Success }
            } catch (e: Exception) {
                logError(e)
                transferState.update { TransferState.Error(errorService.createMessage(e)) }
            }
        }
    }
}
