package cloud.mike.divelog.ui.common.dialogs

import android.content.res.Configuration
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import cloud.mike.divelog.R
import cloud.mike.divelog.ui.DiveTheme
import cloud.mike.divelog.ui.spacing
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    onCancel: () -> Unit,
    onConfirm: (LocalTime) -> Unit,
    modifier: Modifier = Modifier,
    initial: LocalTime? = null,
) {
    var mode: DisplayMode by remember { mutableStateOf(DisplayMode.Picker) }
    val state: TimePickerState = rememberTimePickerState(
        initialHour = initial?.hour ?: 0,
        initialMinute = initial?.minute ?: 0,
    )

    fun onConfirmClicked() {
        val localTime = LocalTime.of(state.hour, state.minute)
        onConfirm(localTime)
    }

    // TimePicker does not provide a default TimePickerDialog, so we use our own PickerDialog:
    // https://issuetracker.google.com/issues/288311426
    PickerDialog(
        modifier = modifier,
        onDismissRequest = onCancel,
        title = { Text(stringResource(R.string.time_picker_title)) },
        buttons = {
            DisplayModeToggleButton(
                displayMode = mode,
                onDisplayModeChange = { mode = it },
            )
            Spacer(Modifier.weight(1f))
            TextButton(onClick = onCancel) {
                Text(stringResource(R.string.common_button_cancel))
            }
            TextButton(onClick = ::onConfirmClicked) {
                Text(stringResource(R.string.common_button_ok))
            }
        },
    ) {
        val contentModifier = Modifier.padding(horizontal = MaterialTheme.spacing.dialogPadding)
        when (mode) {
            DisplayMode.Picker -> TimePicker(modifier = contentModifier, state = state)
            DisplayMode.Input -> TimeInput(modifier = contentModifier, state = state)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DisplayModeToggleButton(
    displayMode: DisplayMode,
    onDisplayModeChange: (DisplayMode) -> Unit,
    modifier: Modifier = Modifier,
) {
    when (displayMode) {
        DisplayMode.Picker -> IconButton(
            modifier = modifier,
            onClick = { onDisplayModeChange(DisplayMode.Input) },
        ) {
            Icon(
                imageVector = Icons.Filled.Keyboard,
                contentDescription = stringResource(R.string.time_picker_button_select_input_mode),
            )
        }
        DisplayMode.Input -> IconButton(
            modifier = modifier,
            onClick = { onDisplayModeChange(DisplayMode.Picker) },
        ) {
            Icon(
                imageVector = Icons.Filled.Schedule,
                contentDescription = stringResource(R.string.time_picker_button_select_picker_mode),
            )
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun Preview() {
    DiveTheme {
        TimePickerDialog(
            onCancel = {},
            onConfirm = {},
        )
    }
}
