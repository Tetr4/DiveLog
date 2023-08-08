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
import cloud.mike.divelog.data.importer.ImportConnectionState
import cloud.mike.divelog.data.importer.ImportConnectionState.BluetoothNotAvailable
import cloud.mike.divelog.data.importer.ImportConnectionState.BluetoothNotEnabled
import cloud.mike.divelog.data.importer.ImportConnectionState.Connected
import cloud.mike.divelog.data.importer.ImportConnectionState.Connecting
import cloud.mike.divelog.data.importer.ImportConnectionState.ConnectionPermissionNotGranted
import cloud.mike.divelog.data.importer.ImportConnectionState.NotConnected
import cloud.mike.divelog.data.importer.ImportConnectionState.NotPaired
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
            ConnectionView(
                uiState = uiState,
                onStartTransfer = onStartTransfer,
            )
        }
    }
}

@Composable
private fun ConnectionView(
    uiState: ImportState,
    onStartTransfer: () -> Unit,
) {
    when (uiState.connectionState) {
        BluetoothNotAvailable -> BluetoothNotAvailableView()
        ConnectionPermissionNotGranted -> MissingPermissionView()
        BluetoothNotEnabled -> BluetoothDisabledView()
        NotPaired -> DeviceNotPairedView()
        is NotConnected -> NotConnectedView()
        is Connecting -> ConnectingView(uiState.connectionState.deviceName)
        is Connected -> TransferView(
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

private class StateProvider : PreviewParameterProvider<ImportConnectionState> {
    override val values = sequenceOf(
        BluetoothNotEnabled,
        ConnectionPermissionNotGranted,
        BluetoothNotEnabled,
        NotPaired,
        NotConnected(deviceName = "Device"),
        Connecting(deviceName = "Device"),
        Connected(deviceName = "Device"),
    )
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun Preview(
    @PreviewParameter(StateProvider::class) state: ImportConnectionState,
) {
    Mdc3Theme {
        Card {
            ImportSheet(
                modifier = Modifier.heightIn(min = 300.dp),
                uiState = ImportState(
                    connectionState = state,
                ),
                onConnect = {},
                onDisconnect = {},
                onStartTransfer = {},
                onShowError = {},
            )
        }
    }
}