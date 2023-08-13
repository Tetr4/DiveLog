package cloud.mike.divelog.ui.imports.states

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cloud.mike.divelog.R
import cloud.mike.divelog.data.dives.Dive
import cloud.mike.divelog.localization.errors.ErrorMessage
import cloud.mike.divelog.ui.DiveTheme

@Composable
fun TransferIdleView(
    onStartTransfer: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        modifier = modifier,
        onClick = onStartTransfer,
    ) {
        Text(stringResource(R.string.import_button_download_dives))
    }
}

@Composable
fun TransferProgressView(
    progress: Float?,
    modifier: Modifier = Modifier,
) {
    if (progress == null) {
        CircularProgressIndicator(modifier = modifier)
    } else {
        CircularProgressIndicator(
            modifier = modifier,
            progress = progress,
            trackColor = MaterialTheme.colorScheme.onSurfaceVariant
                .copy(alpha = 0.4f), // This matches the sheets drag handle
        )
    }
}

@Composable
fun TransferErrorView(
    message: ErrorMessage,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = message.content,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(8.dp))
        Button(onClick = onRetry) {
            Text(stringResource(R.string.common_button_retry))
        }
    }
}

@Composable
fun TransferSuccessView(
    importedDives: List<Dive>,
    modifier: Modifier = Modifier,
) {
    Text(
        modifier = modifier,
        text = pluralStringResource(
            R.plurals.import_status_success,
            importedDives.size,
            importedDives.size,
        ),
        textAlign = TextAlign.Center,
    )
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun PreviewIdle() {
    DiveTheme {
        Card {
            TransferIdleView(
                modifier = Modifier.padding(16.dp),
                onStartTransfer = {},
            )
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun PreviewProgress() {
    DiveTheme {
        Card {
            TransferProgressView(
                modifier = Modifier.padding(16.dp),
                progress = 0.66f,
            )
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun PreviewError() {
    DiveTheme {
        Card {
            TransferErrorView(
                modifier = Modifier.padding(16.dp),
                message = ErrorMessage("Lorem Ipsum"),
                onRetry = {},
            )
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun PreviewSuccess() {
    DiveTheme {
        Card {
            TransferSuccessView(
                modifier = Modifier.padding(16.dp),
                importedDives = Dive.samples,
            )
        }
    }
}
