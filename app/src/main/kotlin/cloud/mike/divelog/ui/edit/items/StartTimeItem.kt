package cloud.mike.divelog.ui.edit.items

import android.content.res.Configuration
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cloud.mike.divelog.R
import cloud.mike.divelog.data.dives.Dive
import cloud.mike.divelog.localization.format
import cloud.mike.divelog.ui.DiveTheme
import cloud.mike.divelog.ui.common.dialogs.TimePickerDialog
import cloud.mike.divelog.ui.common.form.FormTextButton
import cloud.mike.divelog.ui.edit.FormState
import cloud.mike.divelog.ui.edit.rememberFormState
import java.time.format.FormatStyle

@Composable
fun StartTimeItem(
    formState: FormState,
    modifier: Modifier = Modifier,
) {
    var showTimePicker by rememberSaveable { mutableStateOf(false) }

    Row(modifier = modifier) {
        Spacer(Modifier.width(56.dp))
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            FormTextButton(
                modifier = Modifier.weight(1f),
                onClick = { showTimePicker = true },
                value = formState.startTime?.format(FormatStyle.SHORT).orEmpty(),
                placeholder = stringResource(R.string.edit_dive_button_add_time),
            )
            if (formState.startTime != null) {
                IconButton(onClick = { formState.startTime = null }) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = stringResource(R.string.common_button_clear),
                    )
                }
            }
        }
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

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun PreviewEmpty() {
    DiveTheme {
        StartTimeItem(formState = rememberFormState(dive = null))
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun PreviewFilled() {
    DiveTheme {
        StartTimeItem(formState = rememberFormState(dive = Dive.sample))
    }
}
