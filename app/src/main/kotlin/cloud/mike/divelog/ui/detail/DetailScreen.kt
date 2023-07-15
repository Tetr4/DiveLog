package cloud.mike.divelog.ui.detail

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import cloud.mike.divelog.data.dives.Dive
import cloud.mike.divelog.localization.errors.ErrorMessage
import com.google.accompanist.themeadapter.material3.Mdc3Theme

@Composable
fun DetailScreen(
    uiState: DetailState,
    onFetchDive: () -> Unit,
) {
    // TODO implement me
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        when (uiState) {
            is DetailState.Error -> {
                Text(uiState.message.content)
                Button(onClick = onFetchDive) {
                    Text("Retry")
                }
            }

            DetailState.Loading -> CircularProgressIndicator()
            is DetailState.Success -> Text(uiState.dive.location)
        }
    }
}

private class DetailSstatePreviewProvider : PreviewParameterProvider<DetailState> {
    override val values = sequenceOf(
        DetailState.Loading,
        DetailState.Error(ErrorMessage("lorem ipsum")),
        DetailState.Success(Dive.sample),
    )
}

@Preview(showBackground = true, showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun Preview(
    @PreviewParameter(DetailSstatePreviewProvider::class) uiState: DetailState,
) {
    Mdc3Theme {
        DetailScreen(
            uiState = uiState,
            onFetchDive = {},
        )
    }
}
