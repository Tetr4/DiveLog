package cloud.mike.divelog.ui.imports.states

import android.content.res.Configuration
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import cloud.mike.divelog.R
import cloud.mike.divelog.ui.DiveTheme
import cloud.mike.divelog.ui.spacing

@Composable
fun NotConnectedView(modifier: Modifier = Modifier) {
    Text(
        modifier = modifier.padding(MaterialTheme.spacing.sheetPadding),
        text = stringResource(R.string.import_status_not_connected),
        textAlign = TextAlign.Center,
    )
}

@Composable
fun ConnectingView(
    deviceName: String?,
    modifier: Modifier = Modifier,
) {
    Text(
        modifier = modifier.padding(MaterialTheme.spacing.sheetPadding),
        text = if (deviceName != null) {
            stringResource(R.string.import_status_connecting, deviceName)
        } else {
            stringResource(R.string.import_status_connecting_no_device_name)
        },
        textAlign = TextAlign.Center,
    )
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun PreviewNotConnected() {
    DiveTheme {
        Surface {
            NotConnectedView()
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun PreviewConnecting() {
    DiveTheme {
        Surface {
            ConnectingView(deviceName = "OSTC")
        }
    }
}
