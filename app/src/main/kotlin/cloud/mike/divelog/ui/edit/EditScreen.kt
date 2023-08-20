package cloud.mike.divelog.ui.edit

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import cloud.mike.divelog.data.dives.Dive
import cloud.mike.divelog.localization.errors.ErrorMessage
import cloud.mike.divelog.ui.DiveTheme
import cloud.mike.divelog.ui.common.states.ErrorState
import cloud.mike.divelog.ui.common.states.LoadingState
import cloud.mike.divelog.ui.edit.items.DiveStartItem
import cloud.mike.divelog.ui.edit.items.DiveTimeItem
import cloud.mike.divelog.ui.edit.items.NotesItem
import cloud.mike.divelog.ui.edit.topbar.EditDiveAppBar

@OptIn(ExperimentalLayoutApi::class)
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

    suspend fun showError(message: ErrorMessage) {
        snackbarHostState.showSnackbar(message.content)
    }

    fun onSaveClicked() {
        val data = FormData(
            start = formState.start ?: return,
            diveTime = formState.diveTime ?: return,
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
        DiveStartItem(formState)
        Divider()
        DiveTimeItem(formState)
        Divider()
        NotesItem(formState)
        // TODO more data fields
    }
}

@Preview(showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun Preview() {
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
