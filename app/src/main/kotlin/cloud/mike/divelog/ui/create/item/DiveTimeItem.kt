package cloud.mike.divelog.ui.create.item

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Timelapse
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
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
import cloud.mike.divelog.localization.format
import cloud.mike.divelog.ui.DiveTheme
import cloud.mike.divelog.ui.common.picker.DurationPickerDialog
import cloud.mike.divelog.ui.create.FormState
import cloud.mike.divelog.ui.create.rememberFormState

@Composable
fun DiveTimeItem(
    formState: FormState,
    modifier: Modifier = Modifier,
) {
    var showDurationPicker by rememberSaveable { mutableStateOf(false) }

    ListItem(
        modifier = modifier
            .clickable { showDurationPicker = true }
            .semantics { role = Role.Button },
        leadingContent = { Icon(Icons.Default.Timelapse, contentDescription = null) },
        headlineContent = {
            Text(
                text = formState.diveTime?.format()
                    ?: stringResource(cloud.mike.divelog.R.string.create_dive_button_add_duration),
            )
        },
    )

    if (showDurationPicker) {
        DurationPickerDialog(
            initial = formState.diveTime,
            onCancel = { showDurationPicker = false },
            onConfirm = {
                formState.diveTime = it
                showDurationPicker = false
            },
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun Preview() {
    DiveTheme {
        DiveTimeItem(formState = rememberFormState())
    }
}
