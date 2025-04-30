package cloud.mike.divelog.ui.edit.items

import android.content.res.Configuration
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import cloud.mike.divelog.R
import cloud.mike.divelog.data.dives.Dive
import cloud.mike.divelog.localization.format
import cloud.mike.divelog.ui.DiveTheme
import cloud.mike.divelog.ui.common.dialogs.DatePickerDialog
import cloud.mike.divelog.ui.common.form.FormIcon
import cloud.mike.divelog.ui.common.form.FormTextButton
import cloud.mike.divelog.ui.edit.FormState
import cloud.mike.divelog.ui.edit.rememberFormState

@Composable
fun StartDateItem(
    formState: FormState,
    modifier: Modifier = Modifier,
) {
    var showDatePicker by rememberSaveable { mutableStateOf(false) }

    Row(modifier = modifier) {
        FormIcon(imageVector = Icons.Default.Event)
        FormTextButton(
            modifier = Modifier.weight(1f),
            onClick = { showDatePicker = true },
            value = formState.startDate?.format().orEmpty(),
            placeholder = stringResource(R.string.edit_dive_button_add_date),
        )
    }

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
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun PreviewEmpty() {
    DiveTheme {
        StartDateItem(formState = rememberFormState(dive = null))
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun PreviewFilled() {
    DiveTheme {
        StartDateItem(formState = rememberFormState(dive = Dive.sample))
    }
}
