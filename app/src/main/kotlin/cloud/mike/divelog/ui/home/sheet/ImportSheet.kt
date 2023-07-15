package cloud.mike.divelog.ui.home.sheet

import android.Manifest.permission.BLUETOOTH_CONNECT
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import cloud.mike.divelog.bluetooth.connector.ConnectionState
import cloud.mike.divelog.bluetooth.connector.ConnectionState.CONNECTED
import cloud.mike.divelog.bluetooth.connector.ConnectionState.CONNECTING
import cloud.mike.divelog.bluetooth.connector.ConnectionState.IDLE
import cloud.mike.divelog.bluetooth.precondition.PreconditionState
import cloud.mike.divelog.bluetooth.precondition.PreconditionState.BLUETOOTH_CONNECTION_PERMISSION_NOT_GRANTED
import cloud.mike.divelog.bluetooth.precondition.PreconditionState.BLUETOOTH_NOT_AVAILABLE
import cloud.mike.divelog.bluetooth.precondition.PreconditionState.BLUETOOTH_NOT_ENABLED
import cloud.mike.divelog.bluetooth.precondition.PreconditionState.READY
import com.google.accompanist.themeadapter.material3.Mdc3Theme
import org.koin.androidx.compose.koinViewModel

@Composable
fun ImportSheet(viewModel: ImportViewModel = koinViewModel()) {
    AutoConnectEffect(viewModel)
    ImportSheet(
        preconditionState = viewModel.preconditionState,
        connectionState = viewModel.connectionState,
    )
}

@Composable
fun ImportSheet(
    preconditionState: PreconditionState,
    connectionState: ConnectionState,
) {
    val context = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { }

    fun requestBLuetoothPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissionLauncher.launch(BLUETOOTH_CONNECT)
        }
    }

    fun requestEnableBluetooth() {
        if (context.checkSelfPermission(BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
            val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            context.startActivity(intent)
        }
    }

    // TODO implement me
    Column(
        modifier = Modifier
            .fillMaxHeight(0.6f)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(64.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        when (preconditionState) {
            BLUETOOTH_NOT_AVAILABLE -> Text("Bluetooth not available :(")
            BLUETOOTH_CONNECTION_PERMISSION_NOT_GRANTED -> Button(onClick = ::requestBLuetoothPermission) {
                Text("Grant permission")
            }

            BLUETOOTH_NOT_ENABLED -> Button(onClick = ::requestEnableBluetooth) {
                Text("Enable Bluetooth")
            }

            READY -> Text("Ready")
        }
        when (connectionState) {
            IDLE -> Text("Idle")
            CONNECTING -> Text("Connecting...")
            CONNECTED -> Text("Connected!")
        }
    }
}

private class PreconditionProvider : PreviewParameterProvider<PreconditionState> {
    override val values = PreconditionState.values().asSequence()
}

@Preview(showBackground = true, showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun Preview(
    @PreviewParameter(PreconditionProvider::class) preconditionState: PreconditionState,
) {
    Mdc3Theme {
        ImportSheet(
            preconditionState = preconditionState,
            connectionState = IDLE,
        )
    }
}
