package cloud.mike.divelog.ui.common.dialogs

import android.content.res.Configuration
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cloud.mike.divelog.R
import cloud.mike.divelog.ui.DiveTheme
import cloud.mike.divelog.ui.common.picker.DurationPicker
import cloud.mike.divelog.ui.common.picker.rememberDurationPickerState
import cloud.mike.divelog.ui.spacing
import kotlin.time.Duration

@Composable
fun DurationPickerDialog(
    onCancel: () -> Unit,
    onConfirm: (Duration) -> Unit,
    modifier: Modifier = Modifier,
    initial: Duration? = null,
) {
    val state = rememberDurationPickerState(initial ?: Duration.ZERO)

    fun onConfirmClicked() = onConfirm(state.duration)

    PickerDialog(
        modifier = modifier,
        onDismissRequest = onCancel,
        title = {
            Text(stringResource(R.string.duration_picker_title))
        },
        buttons = {
            TextButton(onClick = onCancel) {
                Text(stringResource(R.string.common_button_cancel))
            }
            TextButton(onClick = ::onConfirmClicked) {
                Text(stringResource(R.string.common_button_ok))
            }
        },
    ) {
        DurationPicker(
            modifier = Modifier
                .padding(horizontal = MaterialTheme.spacing.dialogPadding)
                .padding(bottom = 24.dp),
            state = state,
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun Preview() {
    DiveTheme {
        DurationPickerDialog(
            onCancel = {},
            onConfirm = {},
        )
    }
}
