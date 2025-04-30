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
import androidx.compose.material3.ScaffoldDefaults
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
import cloud.mike.divelog.localization.toIntegerOrNull
import cloud.mike.divelog.localization.toNumberOrNull
import cloud.mike.divelog.ui.DiveTheme
import cloud.mike.divelog.ui.common.contentWindowInsetsWithCutout
import cloud.mike.divelog.ui.common.states.ErrorState
import cloud.mike.divelog.ui.common.states.LoadingState
import cloud.mike.divelog.ui.edit.items.BuddyItem
import cloud.mike.divelog.ui.edit.items.DurationItem
import cloud.mike.divelog.ui.edit.items.LocationItem
import cloud.mike.divelog.ui.edit.items.MaxDepthItem
import cloud.mike.divelog.ui.edit.items.NotesItem
import cloud.mike.divelog.ui.edit.items.NumberItem
import cloud.mike.divelog.ui.edit.items.StartDateItem
import cloud.mike.divelog.ui.edit.items.StartTimeItem
import cloud.mike.divelog.ui.edit.topbar.EditDiveAppBar

@Composable
fun EditScreen(
    uiState: EditState,
    onClose: () -> Unit,
    onShowDetail: (Dive) -> Unit,
    onFetchDiveData: () -> Unit,
    onSave: (DiveData) -> Unit,
) {
    val formState = rememberFormState(
        dive = (uiState.diveState as? DiveState.Update)?.dive,
        defaultNumber = (uiState.diveState as? DiveState.Create)?.nextDiveNumber,
    )
    val snackbarHostState = remember { SnackbarHostState() }
    val locale = primaryLocale

    suspend fun showError(message: ErrorMessage) {
        snackbarHostState.showSnackbar(message.content)
    }

    fun onSaveClicked() {
        // Save button should only be enabled if all required fields are set.
        check(formState.canBeSaved)
        val data = DiveData(
            // Format validation is done by text fields, so no need to check here.
            number = formState.number.trim().takeIf { it.isNotBlank() }
                ?.toIntegerOrNull(locale) ?: error("Number is required"),
            startDate = formState.startDate ?: error("Start date is required"),
            startTime = formState.startTime,
            duration = formState.duration ?: error("Duration is required"),
            location = formState.location.trim().takeIf { it.isNotBlank() },
            maxDepthMeters = formState.maxDepthMeters.trim().takeIf { it.isNotBlank() }
                ?.toNumberOrNull(locale)?.toFloat(),
            buddy = formState.buddy.trim().takeIf { it.isNotBlank() },
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
        contentWindowInsets = ScaffoldDefaults.contentWindowInsetsWithCutout,
        topBar = {
            EditDiveAppBar(
                diveState = uiState.diveState,
                saveEnabled = formState.canBeSaved,
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
                onRetry = onFetchDiveData,
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
private fun ContentState(
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
        NumberItem(formState)
        HorizontalDivider(Modifier.padding(vertical = 4.dp))
        StartDateItem(formState)
        StartTimeItem(formState)
        HorizontalDivider(Modifier.padding(vertical = 4.dp))
        DurationItem(formState)
        HorizontalDivider(Modifier.padding(vertical = 4.dp))
        LocationItem(formState)
        HorizontalDivider(Modifier.padding(vertical = 4.dp))
        MaxDepthItem(formState)
        HorizontalDivider(Modifier.padding(vertical = 4.dp))
        BuddyItem(formState)
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
            onFetchDiveData = {},
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
            onFetchDiveData = {},
            onSave = {},
        )
    }
}
