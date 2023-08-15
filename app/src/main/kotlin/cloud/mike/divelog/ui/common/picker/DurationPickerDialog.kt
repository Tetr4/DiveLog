package cloud.mike.divelog.ui.common.picker

import android.content.res.Configuration
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cloud.mike.divelog.R
import cloud.mike.divelog.ui.DiveTheme
import kotlin.time.Duration

@Composable
fun DurationPickerDialog(
    onCancel: () -> Unit,
    onConfirm: (Duration) -> Unit,
    modifier: Modifier = Modifier,
    initial: Duration? = null,
) {
    val state = rememberDurationPickerState(initial ?: Duration.ZERO)
    BasicDialog(
        onDismissRequest = onCancel,
        buttons = {
            TextButton(onClick = onCancel) {
                Text(stringResource(R.string.common_button_cancel))
            }
            TextButton(
                onClick = { onConfirm(state.duration) },
            ) {
                Text(stringResource(R.string.common_button_ok))
            }
        },
    ) {
        Text(
            modifier = modifier
                .align(Alignment.Start)
                .padding(start = 24.dp, end = 24.dp, top = 16.dp, bottom = 20.dp),
            text = stringResource(R.string.duration_picker_title),
            style = MaterialTheme.typography.labelLarge,
            color = AlertDialogDefaults.titleContentColor,
        )
        DurationPicker(
            modifier = Modifier.padding(horizontal = 16.dp),
            state = state,
        )
    }
}

@Preview(showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun Preview() {
    DiveTheme {
        DurationPickerDialog(
            onCancel = {},
            onConfirm = {},
        )
    }
}
