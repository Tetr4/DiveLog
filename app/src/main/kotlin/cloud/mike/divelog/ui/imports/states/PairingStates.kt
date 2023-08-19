package cloud.mike.divelog.ui.imports.states

import android.content.Intent
import android.content.res.Configuration
import android.provider.Settings
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import cloud.mike.divelog.R
import cloud.mike.divelog.ui.DiveTheme
import cloud.mike.divelog.ui.spacing

@Composable
fun DeviceNotPairedView(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    fun showBluetoothSettings() {
        context.startActivity(Intent(Settings.ACTION_BLUETOOTH_SETTINGS))
    }
    Button(
        modifier = modifier.padding(MaterialTheme.spacing.sheetPadding),
        onClick = ::showBluetoothSettings,
    ) {
        Text(stringResource(R.string.import_button_pair))
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun PreviewDeviceNotPaired() {
    DiveTheme {
        Surface {
            DeviceNotPairedView()
        }
    }
}