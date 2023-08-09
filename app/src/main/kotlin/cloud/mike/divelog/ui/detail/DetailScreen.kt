package cloud.mike.divelog.ui.detail

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import cloud.mike.divelog.R
import cloud.mike.divelog.data.dives.Dive
import cloud.mike.divelog.localization.errors.ErrorMessage
import cloud.mike.divelog.ui.DiveTheme
import cloud.mike.divelog.ui.detail.items.InfoItem
import cloud.mike.divelog.ui.detail.items.LocationItem
import cloud.mike.divelog.ui.detail.items.NotesItem
import cloud.mike.divelog.ui.detail.items.ProfileItem

@Composable
fun DetailScreen(
    uiState: DetailState,
    onFetchDive: () -> Unit,
    onNavigateUp: () -> Unit,
) {
    Scaffold(
        topBar = {
            DetailAppBar(
                diveNumber = uiState.dive?.number,
                onNavigateUp = onNavigateUp,
            )
        },
    ) { appBarsPadding ->
        when (uiState) {
            is DetailState.Error -> ErrorState(
                modifier = Modifier.padding(appBarsPadding),
                message = uiState.message,
                onRetry = onFetchDive,
            )
            DetailState.Loading -> LoadingState(
                modifier = Modifier.padding(appBarsPadding),
            )
            is DetailState.Success -> ContentState(
                appBarsPadding = appBarsPadding,
                dive = uiState.dive,
            )
        }
    }
}

@Composable
private fun ErrorState(
    modifier: Modifier = Modifier,
    message: ErrorMessage,
    onRetry: () -> Unit,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(message.content)
        Button(onClick = onRetry) {
            Text(stringResource(R.string.common_button_retry))
        }
    }
}

@Composable
private fun LoadingState(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ContentState(
    appBarsPadding: PaddingValues,
    dive: Dive,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(appBarsPadding)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        InfoItem(
            number = dive.number,
            start = dive.start,
            diveTime = dive.diveTime,
        )
        dive.location?.let {
            LocationItem(it)
        }
        dive.depthProfile?.let {
            ProfileItem(it)
        }
        dive.notes?.let {
            NotesItem(it)
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
    DiveTheme {
        DetailScreen(
            uiState = uiState,
            onFetchDive = {},
            onNavigateUp = {},
        )
    }
}
