package cloud.mike.divelog.ui.detail

import android.content.res.Configuration
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.progressSemantics
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cloud.mike.divelog.R
import cloud.mike.divelog.data.dives.Dive
import cloud.mike.divelog.localization.errors.ErrorMessage
import cloud.mike.divelog.localization.formatDiveNumber
import cloud.mike.divelog.ui.DiveTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailAppBar(
    diveNumber: Int?,
    deleteState: DeleteState,
    onNavigateUp: () -> Unit,
    onDeleteDive: () -> Unit,
    onShowError: suspend (message: ErrorMessage) -> Unit,
    onDismissDeleteError: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LaunchedEffect(deleteState) {
        when (deleteState) {
            is DeleteState.Success -> onNavigateUp() // leave screen after dive was deleted
            is DeleteState.Error -> {
                onShowError(deleteState.message)
                onDismissDeleteError()
            }
            DeleteState.Idle, DeleteState.Loading -> Unit
        }
    }

    CenterAlignedTopAppBar(
        modifier = modifier,
        navigationIcon = {
            NavButton(onClick = onNavigateUp)
        },
        title = {
            if (diveNumber != null) {
                Title(diveNumber)
            }
        },
        actions = {
            if (diveNumber != null) {
                DeleteButton(
                    loading = deleteState is DeleteState.Loading,
                    onClick = onDeleteDive,
                )
            }
        },
    )
}

@Composable
private fun Title(diveNumber: Int) {
    Text(stringResource(R.string.dive_detail_title, diveNumber.formatDiveNumber()))
}

@Composable
private fun NavButton(onClick: () -> Unit) {
    IconButton(onClick) {
        Icon(
            imageVector = Icons.Filled.ArrowBack,
            contentDescription = stringResource(R.string.common_button_navigate_up),
        )
    }
}

@Composable
private fun DeleteButton(
    loading: Boolean,
    onClick: () -> Unit,
) {
    IconButton(
        modifier = if (loading) Modifier.progressSemantics() else Modifier,
        onClick = onClick,
        enabled = !loading,
    ) {
        if (loading) {
            CircularProgressIndicator(Modifier.size(24.dp))
        } else {
            Icon(
                imageVector = Icons.Filled.Delete,
                contentDescription = stringResource(R.string.dive_detail_button_delete_dive),
            )
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun PreviewEmpty() {
    DiveTheme {
        DetailAppBar(
            diveNumber = null,
            deleteState = DeleteState.Idle,
            onNavigateUp = {},
            onDeleteDive = {},
            onShowError = {},
            onDismissDeleteError = {},
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun PreviewLoading() {
    DiveTheme {
        DetailAppBar(
            diveNumber = Dive.sample.number,
            deleteState = DeleteState.Loading,
            onNavigateUp = {},
            onDeleteDive = {},
            onShowError = {},
            onDismissDeleteError = {},
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun Preview() {
    DiveTheme {
        DetailAppBar(
            diveNumber = Dive.sample.number,
            deleteState = DeleteState.Idle,
            onNavigateUp = {},
            onDeleteDive = {},
            onShowError = {},
            onDismissDeleteError = {},
        )
    }
}
