package cloud.mike.divelog.ui.edit.items

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import cloud.mike.divelog.R
import cloud.mike.divelog.localization.format
import cloud.mike.divelog.ui.DiveTheme
import cloud.mike.divelog.ui.common.dialogs.DatePickerDialog
import cloud.mike.divelog.ui.common.dialogs.TimePickerDialog
import cloud.mike.divelog.ui.edit.FormState
import cloud.mike.divelog.ui.edit.rememberFormState
import java.time.format.FormatStyle

@Composable
fun DiveStartItem(
    formState: FormState,
    modifier: Modifier = Modifier,
) {
    var showDatePicker by rememberSaveable { mutableStateOf(false) }
    var showTimePicker by rememberSaveable { mutableStateOf(false) }

    ListItem(
        modifier = modifier,
        leadingContent = { Icon(Icons.Default.Event, contentDescription = null) },
        headlineContent = {
            Text(
                modifier = Modifier
                    .clickable { showDatePicker = true }
                    .semantics { role = Role.Button },
                text = formState.startDate?.format()
                    ?: stringResource(R.string.edit_dive_button_add_date),
            )
        },
        trailingContent = {
            Text(
                modifier = Modifier
                    .clickable { showTimePicker = true }
                    .semantics { role = Role.Button },
                text = formState.startTime?.format(FormatStyle.SHORT)
                    ?: stringResource(R.string.edit_dive_button_add_time),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
        },
    )

    if (showDatePicker) {
        DatePickerDialog(
            initial = formState.startDate,
            onCancel = { showDatePicker = false },
            onConfirm = {
                formState.startDate = it
                showDatePicker = false
            },
        )
    }

    if (showTimePicker) {
        TimePickerDialog(
            initial = formState.startTime,
            onCancel = { showTimePicker = false },
            onConfirm = {
                formState.startTime = it
                showTimePicker = false
            },
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun Preview() {
    DiveTheme {
        DiveStartItem(formState = rememberFormState(dive = null))
    }
}
