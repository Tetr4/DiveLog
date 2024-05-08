package cloud.mike.divelog.ui.edit

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cloud.mike.divelog.data.dives.Dive
import cloud.mike.divelog.localization.errors.ErrorMessage
import cloud.mike.divelog.localization.primaryLocale
import cloud.mike.divelog.localization.toNumberOrNull
import cloud.mike.divelog.ui.DiveTheme
import cloud.mike.divelog.ui.common.states.ErrorState
import cloud.mike.divelog.ui.common.states.LoadingState
import cloud.mike.divelog.ui.edit.items.DiveDurationItem
import cloud.mike.divelog.ui.edit.items.DiveStartDateItem
import cloud.mike.divelog.ui.edit.items.DiveStartTimeItem
import cloud.mike.divelog.ui.edit.items.LocationItem
import cloud.mike.divelog.ui.edit.items.MaxDepthItem
import cloud.mike.divelog.ui.edit.items.NotesItem
import cloud.mike.divelog.ui.edit.topbar.EditDiveAppBar

@Composable
fun EditScreen(
    uiState: EditState,
    onClose: () -> Unit,
    onShowDetail: (Dive) -> Unit,
    onFetchDive: () -> Unit,
    onSave: (FormData) -> Unit,
) {
    val formState = rememberFormState(dive = uiState.diveState.dive)
    val snackbarHostState = remember { SnackbarHostState() }
    val locale = primaryLocale

    suspend fun showError(message: ErrorMessage) {
        snackbarHostState.showSnackbar(message.content)
    }

    fun onSaveClicked() {
        val data = FormData(
            startDate = formState.startDate ?: return,
            startTime = formState.startTime,
            duration = formState.duration ?: return,
            location = formState.location.trim().takeIf { it.isNotBlank() },
            maxDepthMeters = formState.maxDepthMeters.trim().takeIf { it.isNotBlank() }
                ?.toNumberOrNull(locale)?.toFloat(), // validation is done in text field
            notes = formState.notes.trim().takeIf { it.isNotBlank() },
        )
        onSave(data)
    }

    LaunchedEffect(uiState.saveState) {
        when (uiState.saveState) {
            is SaveState.Error -> showError(uiState.saveState.message)
            is SaveState.Idle, is SaveState.Saving -> Unit
            is SaveState.Updated -> onClose()
            is SaveState.Created -> {
                onClose()
                onShowDetail(uiState.saveState.dive)
            }
        }
    }

    Scaffold(
        // TODO show confirmation dialog if unsaved data
        topBar = {
            EditDiveAppBar(
                diveState = uiState.diveState,
                formValid = formState.isValid,
                saving = uiState.saveState is SaveState.Saving,
                onClose = onClose,
                onSave = ::onSaveClicked,
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
            is DiveState.Create, is DiveState.Update -> ContentState(
                modifier = Modifier.consumeWindowInsets(innerPadding),
                contentPadding = innerPadding,
                formState = formState,
            )
        }
    }
}

@Composable
fun ContentState(
    formState: FormState,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(contentPadding),
    ) {
        DiveStartDateItem(formState)
        DiveStartTimeItem(formState)
        HorizontalDivider(Modifier.padding(vertical = 4.dp))
        DiveDurationItem(formState)
        HorizontalDivider(Modifier.padding(vertical = 4.dp))
        LocationItem(formState)
        HorizontalDivider(Modifier.padding(vertical = 4.dp))
        MaxDepthItem(formState)
        HorizontalDivider(Modifier.padding(vertical = 4.dp))
        NotesItem(formState)
    }
}

@Preview(showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun PreviewCreate() {
    DiveTheme {
        EditScreen(
            uiState = EditState(),
            onClose = {},
            onShowDetail = {},
            onFetchDive = {},
            onSave = {},
        )
    }
}

@Preview(showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun PreviewUpdate() {
    DiveTheme {
        EditScreen(
            uiState = EditState(
                diveState = DiveState.Update(dive = Dive.sample),
            ),
            onClose = {},
            onShowDetail = {},
            onFetchDive = {},
            onSave = {},
        )
    }
}
