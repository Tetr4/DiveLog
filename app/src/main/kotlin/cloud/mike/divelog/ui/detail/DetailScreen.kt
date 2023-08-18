package cloud.mike.divelog.ui.detail

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DetailScreen(
    uiState: DetailState,
    onNavigateUp: () -> Unit,
    onFetchDive: () -> Unit,
    onDeleteDive: () -> Unit,
    onDismissDeleteError: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    suspend fun showError(message: ErrorMessage) {
        snackbarHostState.showSnackbar(message.content)
    }

    Scaffold(
        topBar = {
            DetailAppBar(
                diveNumber = uiState.diveState.dive?.number,
                deleteState = uiState.deleteState,
                onNavigateUp = onNavigateUp,
                onDeleteDive = onDeleteDive,
                onShowError = ::showError,
                onDismissDeleteError = onDismissDeleteError,
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        val fullPaddingModifier = Modifier
            .consumeWindowInsets(innerPadding)
            .padding(innerPadding)
        when (uiState.diveState) {
            is DiveState.Loading -> LoadingState(
                modifier = fullPaddingModifier,
            )
            is DiveState.Error -> ErrorState(
                modifier = fullPaddingModifier,
                message = uiState.diveState.message,
                onRetry = onFetchDive,
            )
            is DiveState.Empty -> EmptyState(
                modifier = fullPaddingModifier,
            )
            is DiveState.Content -> ContentState(
                modifier = Modifier
                    .consumeWindowInsets(ScaffoldDefaults.contentWindowInsets)
                    .safeDrawingPadding(),
                contentPadding = innerPadding,
                dive = uiState.diveState.dive,
            )
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
private fun ErrorState(
    message: ErrorMessage,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(message.content)
        Spacer(Modifier.height(8.dp))
        TextButton(onClick = onRetry) {
            Text(stringResource(R.string.common_button_retry))
        }
    }
}

@Composable
private fun EmptyState(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(stringResource(R.string.dive_detail_error_empty))
    }
}

@Composable
private fun ContentState(
    contentPadding: PaddingValues,
    dive: Dive,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(contentPadding)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        InfoItem(
            start = dive.start,
            diveTime = dive.diveTime,
        )
        dive.location?.let {
            LocationItem(it)
        }
        dive.depthProfile?.let {
            ProfileItem(
                profile = it,
                maxDepthMeters = dive.maxDepthMeters,
                minTemperatureCelsius = dive.minTemperatureCelsius,
            )
        }
        dive.notes?.let {
            NotesItem(it)
        }
    }
}

private class PreviewProvider : PreviewParameterProvider<DiveState> {
    override val values = sequenceOf(
        DiveState.Loading,
        DiveState.Error(ErrorMessage("Lorem Ipsum")),
        DiveState.Content(Dive.sample),
        DiveState.Empty,
    )
}

@Preview(showBackground = true, showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun Preview(
    @PreviewParameter(PreviewProvider::class) diveState: DiveState,
) {
    DiveTheme {
        DetailScreen(
            uiState = DetailState(
                diveState = diveState,
            ),
            onNavigateUp = {},
            onFetchDive = {},
            onDeleteDive = {},
            onDismissDeleteError = {},
        )
    }
}
