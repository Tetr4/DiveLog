package cloud.mike.divelog.ui.home.sheet.states

import android.content.res.Configuration
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import cloud.mike.divelog.localization.errors.ErrorMessage
import com.google.accompanist.themeadapter.material3.Mdc3Theme

@Composable
fun TransferIdleView(onStartTransfer: () -> Unit) {
    Button(onClick = onStartTransfer) {
        Text("Download Dives")
    }
}

@Composable
fun TransferProgressView(progress: Float?) {
    if (progress == null) {
        CircularProgressIndicator()
    } else {
        CircularProgressIndicator(progress)
    }
}

@Composable
fun TransferErrorView(
    message: ErrorMessage,
    onRetry: () -> Unit,
) {
    Text(message.content)
    Button(onClick = onRetry) { Text("Retry") }
}

@Composable
fun TransferSuccessView() {
    Text("Success")
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun PreviewIdle() {
    Mdc3Theme { TransferIdleView(onStartTransfer = {}) }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun PreviewProgress() {
    Mdc3Theme { TransferProgressView(0.66f) }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun PreviewError() {
    Mdc3Theme {
        TransferErrorView(
            message = ErrorMessage("Lorem Ipsum"),
            onRetry = {},
        )
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun PreviewSuccess() {
    Mdc3Theme { TransferSuccessView() }
}
