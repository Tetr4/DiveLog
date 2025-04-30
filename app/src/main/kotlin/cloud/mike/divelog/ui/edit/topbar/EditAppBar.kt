package cloud.mike.divelog.ui.edit.topbar

import android.content.res.Configuration
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.progressSemantics
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cloud.mike.divelog.R
import cloud.mike.divelog.data.dives.Dive
import cloud.mike.divelog.ui.DiveTheme
import cloud.mike.divelog.ui.common.windowInsetsWithCutout
import cloud.mike.divelog.ui.edit.DiveState
import cloud.mike.divelog.ui.spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditDiveAppBar(
    diveState: DiveState,
    saveEnabled: Boolean,
    saving: Boolean,
    onClose: () -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        modifier = modifier,
        windowInsets = TopAppBarDefaults.windowInsetsWithCutout,
        navigationIcon = { CloseButton(onClick = onClose) },
        title = { Title(diveState) },
        actions = {
            when (diveState) {
                is DiveState.Error, is DiveState.Loading -> Unit
                is DiveState.Create, is DiveState.Update -> SaveButton(
                    modifier = Modifier.padding(end = MaterialTheme.spacing.screenPadding),
                    enabled = saveEnabled,
                    loading = saving,
                    onClick = onSave,
                )
            }
        },
    )
}

@Composable
private fun Title(diveState: DiveState) {
    when (diveState) {
        is DiveState.Create -> Text(stringResource(R.string.edit_dive_title_create))
        is DiveState.Update -> Text(stringResource(R.string.edit_dive_title_update))
        is DiveState.Loading, is DiveState.Error -> Unit
    }
}

@Composable
private fun CloseButton(onClick: () -> Unit) {
    IconButton(onClick) {
        Icon(
            imageVector = Icons.Filled.Close,
            contentDescription = stringResource(R.string.common_button_cancel),
        )
    }
}

@Composable
private fun SaveButton(
    enabled: Boolean,
    loading: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        modifier = if (loading) modifier.progressSemantics() else modifier,
        enabled = enabled && !loading,
        onClick = onClick,
    ) {
        if (loading) {
            CircularProgressIndicator(Modifier.size(24.dp))
        } else {
            Text(stringResource(R.string.common_button_save))
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun PreviewEmpty() {
    DiveTheme {
        EditDiveAppBar(
            diveState = DiveState.Loading,
            saveEnabled = false,
            saving = false,
            onClose = {},
            onSave = {},
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun PreviewUpdate() {
    DiveTheme {
        EditDiveAppBar(
            diveState = DiveState.Update(Dive.sample),
            saveEnabled = true,
            saving = false,
            onClose = {},
            onSave = {},
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun PreviewCreate() {
    DiveTheme {
        EditDiveAppBar(
            diveState = DiveState.Create(nextDiveNumber = 42),
            saveEnabled = false,
            saving = true,
            onClose = {},
            onSave = {},
        )
    }
}
