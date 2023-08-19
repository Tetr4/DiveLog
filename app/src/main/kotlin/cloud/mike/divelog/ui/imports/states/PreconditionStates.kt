package cloud.mike.divelog.ui.imports.states

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import cloud.mike.divelog.R
import cloud.mike.divelog.ui.DiveTheme
import cloud.mike.divelog.ui.spacing

@Composable
fun BluetoothNotAvailableView(modifier: Modifier = Modifier) {
    Text(
        modifier = modifier.padding(MaterialTheme.spacing.sheetPadding),
        text = stringResource(R.string.import_status_bluetooth_not_available),
        textAlign = TextAlign.Center,
    )
}

@Composable
fun MissingPermissionView(modifier: Modifier = Modifier) {
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {}
    fun requestBLuetoothPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissionLauncher.launch(Manifest.permission.BLUETOOTH_CONNECT)
        }
    }
    Button(
        modifier = modifier.padding(MaterialTheme.spacing.sheetPadding),
        onClick = ::requestBLuetoothPermission,
    ) {
        Text(stringResource(R.string.import_button_request_permission))
    }
}

@Composable
fun BluetoothDisabledView(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    fun requestEnableBluetooth() {
        if (context.checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
            val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            context.startActivity(intent)
        }
    }
    Button(
        modifier = modifier.padding(MaterialTheme.spacing.sheetPadding),
        onClick = ::requestEnableBluetooth,
    ) {
        Text(stringResource(R.string.import_button_enable_bluetooth))
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun PreviewBluetoothNotAvailable() {
    DiveTheme {
        Surface {
            BluetoothNotAvailableView()
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun PreviewMissingPermission() {
    DiveTheme {
        Surface {
            MissingPermissionView()
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun PreviewBluetoothDisabled() {
    DiveTheme {
        Surface {
            BluetoothDisabledView()
        }
    }
}
