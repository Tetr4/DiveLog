package cloud.mike.divelog.ui.detail

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import cloud.mike.divelog.data.dives.Dive
import cloud.mike.divelog.localization.errors.ErrorMessage
import cloud.mike.divelog.ui.DiveTheme
import cloud.mike.divelog.ui.common.states.ErrorState
import cloud.mike.divelog.ui.common.states.LoadingState
import cloud.mike.divelog.ui.detail.items.InfoItem
import cloud.mike.divelog.ui.detail.items.LocationItem
import cloud.mike.divelog.ui.detail.items.NotesItem
import cloud.mike.divelog.ui.detail.items.ProfileItem
import cloud.mike.divelog.ui.detail.states.EmptyState
import cloud.mike.divelog.ui.detail.topbar.DetailAppBar
import cloud.mike.divelog.ui.spacing

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DetailScreen(
    uiState: DetailState,
    onNavigateUp: () -> Unit,
    onShowEdit: (Dive) -> Unit,
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
                dive = uiState.diveState.dive,
                deleteState = uiState.deleteState,
                onNavigateUp = onNavigateUp,
                onShowEdit = onShowEdit,
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
                modifier = Modifier.consumeWindowInsets(innerPadding),
                contentPadding = innerPadding,
                dive = uiState.diveState.dive,
            )
        }
    }
}

@Composable
private fun ContentState(
    dive: Dive,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(contentPadding)
            .padding(MaterialTheme.spacing.screenPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        InfoItem(
            startDate = dive.startDate,
            startTime = dive.startTime,
            duration = dive.duration,
        )
        dive.location?.let {
            LocationItem(it)
        }
        dive.profile?.let {
            ProfileItem(
                profile = it,
                maxDepthMeters = dive.maxDepthMeters, // TODO show max depth even without profile
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

@Preview(showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
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
            onShowEdit = {},
            onFetchDive = {},
            onDeleteDive = {},
            onDismissDeleteError = {},
        )
    }
}
