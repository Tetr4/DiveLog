@file:OptIn(ExperimentalMaterial3Api::class)

package cloud.mike.divelog.ui.common.picker

import android.content.res.Configuration
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import cloud.mike.divelog.R
import cloud.mike.divelog.ui.DiveTheme
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

@Composable
fun DatePickerDialogSimple(
    onCancel: () -> Unit,
    onConfirm: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
    initial: LocalDate? = null,
) {
    val zone = ZoneOffset.UTC // DatePicker uses UTC
    val state: DatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initial?.atStartOfDay(zone)?.toInstant()?.toEpochMilli(),
    )

    DatePickerDialog(
        modifier = modifier,
        onDismissRequest = onCancel,
        dismissButton = {
            TextButton(onClick = onCancel) {
                Text(stringResource(R.string.common_button_cancel))
            }
        },
        confirmButton = {
            TextButton(
                enabled = state.selectedDateMillis != null,
                onClick = {
                    val instant = state.selectedDateMillis?.let(Instant::ofEpochMilli) ?: error("Missing date")
                    val localDate = instant.atZone(zone).toLocalDate()
                    onConfirm(localDate)
                },
            ) {
                Text(stringResource(R.string.common_button_ok))
            }
        },
    ) {
        DatePicker(state = state)
    }
}

@Preview(showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun Preview() {
    DiveTheme {
        DatePickerDialogSimple(
            onCancel = {},
            onConfirm = {},
        )
    }
}
