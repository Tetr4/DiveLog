package cloud.mike.divelog.ui.edit.items

import android.content.res.Configuration
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import cloud.mike.divelog.R
import cloud.mike.divelog.data.dives.Dive
import cloud.mike.divelog.ui.DiveTheme
import cloud.mike.divelog.ui.common.form.FormIcon
import cloud.mike.divelog.ui.common.form.FormTextField
import cloud.mike.divelog.ui.edit.FormState
import cloud.mike.divelog.ui.edit.rememberFormState

@Composable
fun BuddyItem(
    formState: FormState,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier) {
        FormIcon(Icons.Filled.Group)
        FormTextField(
            modifier = Modifier.weight(1f),
            value = formState.buddy,
            onValueChange = { formState.buddy = it },
            placeholder = stringResource(R.string.edit_dive_placeholder_add_buddy),
            keyboardOptions = KeyboardOptions.Default.copy(
                capitalization = KeyboardCapitalization.Words,
            ),
        )
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun PreviewEmpty() {
    DiveTheme {
        BuddyItem(formState = rememberFormState(dive = null))
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun PreviewFilled() {
    DiveTheme {
        BuddyItem(formState = rememberFormState(dive = Dive.sample))
    }
}
