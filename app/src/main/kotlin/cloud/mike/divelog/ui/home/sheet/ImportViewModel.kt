package cloud.mike.divelog.ui.home.sheet

import android.bluetooth.BluetoothDevice
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cloud.mike.divelog.bluetooth.connector.AutoConnector
import cloud.mike.divelog.bluetooth.connector.ConnectionState
import cloud.mike.divelog.bluetooth.device.DeviceProvider
import cloud.mike.divelog.bluetooth.precondition.PreconditionService
import cloud.mike.divelog.bluetooth.precondition.PreconditionState
import cloud.mike.divelog.data.communication.exitCommunication
import cloud.mike.divelog.data.communication.sendCompactHeaders
import cloud.mike.divelog.data.communication.startCommunication
import cloud.mike.divelog.data.logging.logError
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
        val connection = autoConnector.connection ?: return
        viewModelScope.launch {
            try {
                transferState.update { TransferState.Transfering(progress = null) }
                // TODO implement me
                connection.startCommunication()
                delay(3000)
                val result = connection.sendCompactHeaders()
                Log.d("Import", "Headers: $result")
                delay(3000)
                connection.exitCommunication()
                transferState.update { TransferState.Success }
            } catch (e: Exception) {
                logError(e)
                transferState.update { TransferState.Error(errorService.createMessage(e)) }
            }
        }
    }
}
