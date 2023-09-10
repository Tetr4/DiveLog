package cloud.mike.divelog.ui.edit.items

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cloud.mike.divelog.R
import cloud.mike.divelog.ui.DiveTheme
import cloud.mike.divelog.ui.common.text.PlainTextField
import cloud.mike.divelog.ui.edit.FormState
import cloud.mike.divelog.ui.edit.rememberFormState

@Composable
fun LocationItem(
    formState: FormState,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.Top,
    ) {
        LocationIcon()
        PlainTextField(
            modifier = Modifier.weight(1f),
            value = formState.location,
            onValueChange = { formState.location = it },
            placeholder = stringResource(R.string.edit_dive_placeholder_add_location),
        )
    }
}

@Composable
private fun LocationIcon() {
    Box(
        modifier = Modifier.size(56.dp),
        contentAlignment = Alignment.Center,
    ) {
        Icon(Icons.Default.LocationOn, contentDescription = null)
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun Preview() {
    DiveTheme {
        LocationItem(formState = rememberFormState(dive = null))
    }
}