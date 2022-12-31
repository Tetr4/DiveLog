package cloud.mike.divelog.ui.import

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cloud.mike.divelog.bluetooth.connector.AutoConnector
import cloud.mike.divelog.bluetooth.precondition.PreconditionService
import kotlinx.coroutines.launch
import kotlinx.coroutines.rx2.asFlow

class ImportViewModel(
    preconditionService: PreconditionService,
    private val autoConnector: AutoConnector,
) : ViewModel() {

    var preconditionState by mutableStateOf(preconditionService.precondition)
    var connectionState by mutableStateOf(autoConnector.connectionState)
    var error: Exception? by mutableStateOf(null)

    init {
        autoConnector.connect()
        viewModelScope.launch {
            preconditionService.preconditionStream.asFlow().collect { preconditionState = it }
        }
        viewModelScope.launch {
            autoConnector.connectionStateStream.asFlow().collect { connectionState = it }
        }
        viewModelScope.launch {
            autoConnector.error.asFlow().collect { error = it }
        }
    }

    override fun onCleared() {
        autoConnector.disconnect()
        super.onCleared()
    }

    fun connect() = autoConnector.connect()

    fun disconnect() = autoConnector.disconnect()
}
