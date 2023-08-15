@file:OptIn(ExperimentalMaterial3Api::class)

package cloud.mike.divelog.ui.common.picker

import android.content.res.Configuration
import android.widget.TimePicker
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialogDefaults.titleContentColor
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cloud.mike.divelog.R
import cloud.mike.divelog.ui.DiveTheme
import java.time.LocalTime

@Composable
fun TimePickerDialogSimple(
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

    TimePickerDialog(
        modifier = modifier,
        onDismissRequest = onCancel,
        confirmButton = {
            TextButton(
                onClick = {
                    val localTime = LocalTime.of(state.hour, state.minute)
                    onConfirm(localTime)
                },
            ) {
                Text(stringResource(R.string.common_button_ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onCancel) {
                Text(stringResource(R.string.common_button_cancel))
            }
        },
        title = { Text(stringResource(R.string.time_picker_title)) },
        modeToggleButton = {
            DisplayModeToggleButton(
                displayMode = mode,
                onDisplayModeChange = { mode = it },
            )
        },
    ) {
        Box(Modifier.padding(horizontal = 16.dp)) {
            when (mode) {
                DisplayMode.Picker -> TimePicker(state = state)
                DisplayMode.Input -> TimeInput(state = state)
            }
        }
    }
}

/**
 * For some reason there is no [TimePicker] equivalent of [DatePickerDialog].
 *
 * See [issue 288311426](https://issuetracker.google.com/issues/288311426).
 */
@Composable
private fun TimePickerDialog(
    onDismissRequest: () -> Unit,
    title: @Composable () -> Unit,
    modeToggleButton: @Composable () -> Unit,
    dismissButton: @Composable () -> Unit,
    confirmButton: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    BasicDialog(
        modifier = modifier,
        onDismissRequest = onDismissRequest,
        buttons = {
            Row(
                modifier = Modifier.padding(bottom = 8.dp, end = 6.dp, start = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                modeToggleButton()
                Spacer(Modifier.weight(1f))
                dismissButton()
                confirmButton()
            }
        },
    ) {
        CompositionLocalProvider(LocalContentColor provides titleContentColor) {
            ProvideTextStyle(MaterialTheme.typography.labelLarge) {
                Box(
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(start = 24.dp, end = 24.dp, top = 16.dp, bottom = 20.dp),
                ) {
                    title()
                }
            }
        }
        content()
    }
}

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

@Preview(showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun Preview() {
    DiveTheme {
        TimePickerDialogSimple(
            onCancel = {},
            onConfirm = {},
        )
    }
}
