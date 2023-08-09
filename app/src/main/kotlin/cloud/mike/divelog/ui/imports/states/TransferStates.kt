package cloud.mike.divelog.ui.imports.states

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import cloud.mike.divelog.R
import cloud.mike.divelog.data.dives.Dive
import cloud.mike.divelog.localization.errors.ErrorMessage
import cloud.mike.divelog.ui.DiveTheme

@Composable
fun TransferIdleView(onStartTransfer: () -> Unit) {
    Button(onClick = onStartTransfer) {
        Text(stringResource(R.string.import_button_download_dives))
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
        Button(onClick = onRetry) {
            Text(stringResource(R.string.common_button_retry))
        }
    }
}

@Composable
fun TransferSuccessView(importedDives: List<Dive>) {
    Text(
        text = pluralStringResource(
            R.plurals.import_status_success,
            importedDives.size,
            importedDives.size,
        ),
    )
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
    DiveTheme {
        TransferSuccessView(
            importedDives = Dive.samples,
        )
    }
}
