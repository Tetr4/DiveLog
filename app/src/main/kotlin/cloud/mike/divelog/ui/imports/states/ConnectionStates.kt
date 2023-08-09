package cloud.mike.divelog.ui.imports.states

import android.content.res.Configuration
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import cloud.mike.divelog.R
import cloud.mike.divelog.ui.DiveTheme

@Composable
fun NotConnectedView() {
    Text(stringResource(R.string.import_status_not_connected))
}

@Composable
fun ConnectingView(deviceName: String?) {
    val text = if (deviceName != null) {
        stringResource(R.string.import_status_connecting, deviceName)
    } else {
        stringResource(R.string.import_status_connecting_no_device_name)
    }
    Text(text)
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun PreviewNotConnected() {
    DiveTheme { NotConnectedView() }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun PreviewConnecting() {
    DiveTheme { ConnectingView(deviceName = "OSTC") }
}
