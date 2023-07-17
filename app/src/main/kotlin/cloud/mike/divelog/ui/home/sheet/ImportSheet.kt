package cloud.mike.divelog.ui.home.sheet

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cloud.mike.divelog.bluetooth.connector.ConnectionState.CONNECTED
import cloud.mike.divelog.bluetooth.connector.ConnectionState.CONNECTING
import cloud.mike.divelog.bluetooth.connector.ConnectionState.IDLE
import cloud.mike.divelog.bluetooth.precondition.PreconditionState
import cloud.mike.divelog.bluetooth.precondition.PreconditionState.BLUETOOTH_CONNECTION_PERMISSION_NOT_GRANTED
import cloud.mike.divelog.bluetooth.precondition.PreconditionState.BLUETOOTH_NOT_AVAILABLE
import cloud.mike.divelog.bluetooth.precondition.PreconditionState.BLUETOOTH_NOT_ENABLED
import cloud.mike.divelog.bluetooth.precondition.PreconditionState.READY
import cloud.mike.divelog.bluetooth.utils.aliasOrName
import cloud.mike.divelog.localization.errors.ErrorMessage
import cloud.mike.divelog.ui.common.LifecycleEffect
import cloud.mike.divelog.ui.home.sheet.states.BluetoothDisabledView
import cloud.mike.divelog.ui.home.sheet.states.BluetoothNotAvailableView
import cloud.mike.divelog.ui.home.sheet.states.ConnectingView
import cloud.mike.divelog.ui.home.sheet.states.DeviceNotPairedView
import cloud.mike.divelog.ui.home.sheet.states.MissingPermissionView
import cloud.mike.divelog.ui.home.sheet.states.NotConnectedView
import cloud.mike.divelog.ui.home.sheet.states.TransferErrorView
import cloud.mike.divelog.ui.home.sheet.states.TransferIdleView
import cloud.mike.divelog.ui.home.sheet.states.TransferProgressView
import cloud.mike.divelog.ui.home.sheet.states.TransferSuccessView
import com.google.accompanist.themeadapter.material3.Mdc3Theme
import org.koin.androidx.compose.koinViewModel

@Composable
fun ImportSheet(
    onShowError: suspend (ErrorMessage) -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: ImportViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    ImportSheet(
        modifier = modifier,
        uiState = uiState,
        onConnect = viewModel::connect,
        onDisconnect = viewModel::disconnect,
        onStartTransfer = viewModel::startTransfer,
        onShowError = onShowError,
    )
}

@Composable
private fun ImportSheet(
    uiState: ImportState,
    onConnect: () -> Unit,
    onDisconnect: () -> Unit,
    onStartTransfer: () -> Unit,
    onShowError: suspend (ErrorMessage) -> Unit,
    modifier: Modifier = Modifier,
) {
    LifecycleEffect(Lifecycle.Event.ON_RESUME, onConnect)
    LifecycleEffect(Lifecycle.Event.ON_PAUSE, onDisconnect)

    LaunchedEffect(uiState.connectionError) {
        uiState.connectionError?.let { onShowError(it) }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(64.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        when (uiState.preconditionState) {
            BLUETOOTH_NOT_AVAILABLE -> BluetoothNotAvailableView()
            BLUETOOTH_CONNECTION_PERMISSION_NOT_GRANTED -> MissingPermissionView()
            BLUETOOTH_NOT_ENABLED -> BluetoothDisabledView()
            READY -> if (uiState.device == null) {
                DeviceNotPairedView()
            } else {
                ReadyView(
                    uiState = uiState,
                    deviceName = uiState.device.aliasOrName,
                    onStartTransfer = onStartTransfer,
                )
            }
        }
    }
}

@Composable
private fun ReadyView(
    uiState: ImportState,
    deviceName: String?,
    onStartTransfer: () -> Unit,
) {
    when (uiState.connectionState) {
        IDLE -> NotConnectedView()
        CONNECTING -> ConnectingView(deviceName)
        CONNECTED -> TransferView(
            transferState = uiState.transferState,
            onStartTransfer = onStartTransfer,
        )
    }
}

@Composable
private fun TransferView(
    transferState: TransferState,
    onStartTransfer: () -> Unit,
) {
    when (transferState) {
        TransferState.Idle -> TransferIdleView(onStartTransfer = onStartTransfer)
        is TransferState.Transfering -> TransferProgressView(transferState.progress)
        is TransferState.Error -> TransferErrorView(transferState.message, onRetry = onStartTransfer)
        TransferState.Success -> TransferSuccessView()
    }
}

private class PreconditionProvider : PreviewParameterProvider<PreconditionState> {
    override val values = PreconditionState.values().asSequence()
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun Preview(
    @PreviewParameter(PreconditionProvider::class) preconditionState: PreconditionState,
) {
    Mdc3Theme {
        Card {
            ImportSheet(
                modifier = Modifier.heightIn(min = 300.dp),
                uiState = ImportState(
                    preconditionState = preconditionState,
                    connectionState = IDLE,
                ),
                onConnect = {},
                onDisconnect = {},
                onStartTransfer = {},
                onShowError = {},
            )
        }
    }
}
