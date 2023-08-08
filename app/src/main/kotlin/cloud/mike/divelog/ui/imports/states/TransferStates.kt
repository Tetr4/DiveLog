package cloud.mike.divelog.ui.imports.states

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import cloud.mike.divelog.localization.errors.ErrorMessage
import cloud.mike.divelog.ui.DiveTheme

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
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(message.content)
        Button(onClick = onRetry) { Text("Retry") }
    }
}

@Composable
fun TransferSuccessView() {
    Text("Success")
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun PreviewIdle() {
    DiveTheme { TransferIdleView(onStartTransfer = {}) }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun PreviewProgress() {
    DiveTheme { TransferProgressView(0.66f) }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun PreviewError() {
    DiveTheme {
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
    DiveTheme { TransferSuccessView() }
}
