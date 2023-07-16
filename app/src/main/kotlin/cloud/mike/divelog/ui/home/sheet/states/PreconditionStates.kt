package cloud.mike.divelog.ui.home.sheet.states

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.google.accompanist.themeadapter.material3.Mdc3Theme

@Composable
fun BluetoothNotAvailableView() {
    Text("Bluetooth not available :(")
}

@Composable
fun MissingPermissionView() {
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { }
    fun requestBLuetoothPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissionLauncher.launch(Manifest.permission.BLUETOOTH_CONNECT)
        }
    }
    Button(onClick = ::requestBLuetoothPermission) {
        Text("Grant Bluetooth permission")
    }
}

@Composable
fun BluetoothDisabledView() {
    val context = LocalContext.current
    fun requestEnableBluetooth() {
        if (context.checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
            val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            context.startActivity(intent)
        }
    }
    Button(onClick = ::requestEnableBluetooth) {
        Text("Enable Bluetooth")
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun PreviewBluetoothNotAvailable() {
    Mdc3Theme { BluetoothNotAvailableView() }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun PreviewMissingPermission() {
    Mdc3Theme { MissingPermissionView() }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun PreviewBluetoothDisabled() {
    Mdc3Theme { BluetoothDisabledView() }
}
