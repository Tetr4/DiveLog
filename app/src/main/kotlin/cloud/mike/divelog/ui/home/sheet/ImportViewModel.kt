package cloud.mike.divelog.ui.home.sheet

import android.bluetooth.BluetoothDevice
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cloud.mike.divelog.bluetooth.connector.AutoConnector
import cloud.mike.divelog.bluetooth.connector.ConnectionState
import cloud.mike.divelog.bluetooth.device.DeviceProvider
import cloud.mike.divelog.bluetooth.precondition.PreconditionService
import cloud.mike.divelog.bluetooth.precondition.PreconditionState
import cloud.mike.divelog.localization.errors.ErrorMessage
import cloud.mike.divelog.localization.errors.ErrorService
import kotlinx.coroutines.delay
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
    val device: BluetoothDevice? = null,
    val preconditionState: PreconditionState,
    val connectionState: ConnectionState,
    val transferState: TransferState = TransferState.Idle,
    val connectionError: ErrorMessage? = null,
)

class ImportViewModel(
    deviceProvider: DeviceProvider,
    preconditionService: PreconditionService,
    private val autoConnector: AutoConnector,
    private val errorService: ErrorService,
) : ViewModel() {

    private val transferState = MutableStateFlow<TransferState>(TransferState.Idle)

    val uiState = combine(
        deviceProvider.deviceFlow.onStart { emit(deviceProvider.device) },
        preconditionService.preconditionFlow.onStart { emit(preconditionService.precondition) },
        autoConnector.connectionStateFlow.onStart { emit(autoConnector.connectionState) },
        transferState,
        autoConnector.error
            .map<Exception, ErrorMessage?> { errorService.createMessage(it) }
            .onStart { emit(null) },
        ::ImportState,
    ).stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        ImportState(
            preconditionState = preconditionService.precondition,
            connectionState = autoConnector.connectionState,
        ),
    )

    fun connect() = autoConnector.connect()

    fun disconnect() = autoConnector.disconnect()

    fun startTransfer() {
        viewModelScope.launch {
            try {
                transferState.update { TransferState.Transfering(progress = null) }
                // TODO implement me
                repeat(100) { step ->
                    delay(10)
                    transferState.update { TransferState.Transfering(progress = step / 100f) }
                }
                transferState.update { TransferState.Success }
            } catch (e: Exception) {
                transferState.update { TransferState.Error(errorService.createMessage(e)) }
            }
        }
    }
}
