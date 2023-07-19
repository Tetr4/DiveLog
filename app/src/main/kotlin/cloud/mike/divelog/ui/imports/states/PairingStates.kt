package cloud.mike.divelog.ui.imports.states

import android.content.Intent
import android.content.res.Configuration
import android.provider.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.google.accompanist.themeadapter.material3.Mdc3Theme

@Composable
fun DeviceNotPairedView() {
    val context = LocalContext.current
    fun showBluetoothSettings() {
        context.startActivity(Intent(Settings.ACTION_BLUETOOTH_SETTINGS))
    }
    Button(onClick = ::showBluetoothSettings) {
        Text("Pair device")
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun PreviewDeviceNotPaired() {
    Mdc3Theme { DeviceNotPairedView() }
}
