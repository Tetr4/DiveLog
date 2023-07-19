package cloud.mike.divelog.ui.imports.states

import android.content.res.Configuration
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.google.accompanist.themeadapter.material3.Mdc3Theme

@Composable
fun NotConnectedView() {
    Text("Not connected")
}

@Composable
fun ConnectingView(deviceName: String?) {
    Text("Connecting to $deviceName...")
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun PreviewNotConnected() {
    Mdc3Theme { NotConnectedView() }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun PreviewConnecting() {
    Mdc3Theme { ConnectingView(deviceName = "OSTC") }
}
