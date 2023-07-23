package cloud.mike.divelog.ui.imports

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
import cloud.mike.divelog.data.importer.DeviceConnectionState
import cloud.mike.divelog.localization.errors.ErrorMessage
import cloud.mike.divelog.ui.common.LifecycleEffect
import cloud.mike.divelog.ui.imports.states.BluetoothDisabledView
import cloud.mike.divelog.ui.imports.states.BluetoothNotAvailableView
import cloud.mike.divelog.ui.imports.states.ConnectingView
import cloud.mike.divelog.ui.imports.states.DeviceNotPairedView
import cloud.mike.divelog.ui.imports.states.MissingPermissionView
import cloud.mike.divelog.ui.imports.states.NotConnectedView
import cloud.mike.divelog.ui.imports.states.TransferErrorView
import cloud.mike.divelog.ui.imports.states.TransferIdleView
import cloud.mike.divelog.ui.imports.states.TransferProgressView
import cloud.mike.divelog.ui.imports.states.TransferSuccessView
import com.google.accompanist.themeadapter.material3.Mdc3Theme
import com.sebaslogen.resaca.viewModelScoped
import org.koin.java.KoinJavaComponent.getKoin

@Composable
fun ImportSheet(
    onShowError: suspend (ErrorMessage) -> Unit,
    modifier: Modifier = Modifier,
) {
    // Viewmodel is scoped to sheet, so a new one is created when closing and opening sheet.
    val viewModel: ImportViewModel = viewModelScoped { getKoin().get() }
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
    val transferFinished = uiState.transferState == TransferState.Success

    // Autoconnect when app is in foreground
    if (!transferFinished) {
        LifecycleEffect(Lifecycle.Event.ON_RESUME, onConnect)
        LifecycleEffect(Lifecycle.Event.ON_PAUSE, onDisconnect)
    }

    // Disconnect after transmission
    LaunchedEffect(transferFinished) {
        if (transferFinished) onDisconnect()
    }

    LaunchedEffect(uiState.connectionError) {
        uiState.connectionError?.let { onShowError(it) }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(64.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (transferFinished) {
            TransferSuccessView()
        } else {
            PreconditionView(
                uiState = uiState,
                onStartTransfer = onStartTransfer,
            )
        }
    }
}

@Composable
private fun PreconditionView(
    uiState: ImportState,
    onStartTransfer: () -> Unit,
) {
    when (uiState.deviceConnectionState) {
        DeviceConnectionState.BluetoothNotAvailable -> BluetoothNotAvailableView()
        DeviceConnectionState.ConnectionPermissionNotGranted -> MissingPermissionView()
        DeviceConnectionState.BluetoothNotEnabled -> BluetoothDisabledView()
        DeviceConnectionState.NotPaired -> DeviceNotPairedView()
        is DeviceConnectionState.NotConnected -> NotConnectedView()
        is DeviceConnectionState.Connecting -> ConnectingView(uiState.deviceConnectionState.deviceName)
        is DeviceConnectionState.Connected -> TransferView(
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

private class StateProvider : PreviewParameterProvider<DeviceConnectionState> {
    override val values = sequenceOf(
        DeviceConnectionState.BluetoothNotEnabled,
        DeviceConnectionState.ConnectionPermissionNotGranted,
        DeviceConnectionState.BluetoothNotEnabled,
        DeviceConnectionState.NotPaired,
        DeviceConnectionState.NotConnected(deviceName = "Device"),
        DeviceConnectionState.Connecting(deviceName = "Device"),
        DeviceConnectionState.Connected(deviceName = "Device"),
    )
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun Preview(
    @PreviewParameter(StateProvider::class) state: DeviceConnectionState,
) {
    Mdc3Theme {
        Card {
            ImportSheet(
                modifier = Modifier.heightIn(min = 300.dp),
                uiState = ImportState(
                    deviceConnectionState = state,
                ),
                onConnect = {},
                onDisconnect = {},
                onStartTransfer = {},
                onShowError = {},
            )
        }
    }
}
