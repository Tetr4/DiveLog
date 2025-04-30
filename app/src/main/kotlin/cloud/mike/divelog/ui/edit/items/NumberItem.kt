package cloud.mike.divelog.ui.edit.items

import android.content.res.Configuration
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cloud.mike.divelog.R
import cloud.mike.divelog.data.dives.Dive
import cloud.mike.divelog.localization.isValidInteger
import cloud.mike.divelog.localization.primaryLocale
import cloud.mike.divelog.ui.DiveTheme
import cloud.mike.divelog.ui.common.form.FormIcon
import cloud.mike.divelog.ui.common.form.FormTextField
import cloud.mike.divelog.ui.edit.FormState
import cloud.mike.divelog.ui.edit.rememberFormState

@Composable
fun NumberItem(
    formState: FormState,
    modifier: Modifier = Modifier,
) {
    val locale = primaryLocale
    val isEmpty = formState.number.isEmpty()
    Row(modifier = modifier) {
        FormIcon(Icons.Default.Numbers)
        FormTextField(
            modifier = Modifier
                .weight(1f)
                .padding(end = 16.dp),
            singleLine = true,
            value = formState.number,
            onValueChange = {
                if (it.isEmpty() || it.isValidInteger(locale)) formState.number = it
            },
            placeholder = stringResource(R.string.edit_dive_placeholder_add_number),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done,
            ),
            isError = isEmpty,
            trailingIcon = if (isEmpty) {
                { ErrorIcon() }
            } else null,
        )
    }
}

@Composable
private fun ErrorIcon() {
    Icon(Icons.Default.Error, stringResource(R.string.common_error_field_required))
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun PreviewEmpty() {
    DiveTheme {
        NumberItem(formState = rememberFormState(dive = null))
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun PreviewFilled() {
    DiveTheme {
        NumberItem(formState = rememberFormState(dive = Dive.sample))
    }
}
