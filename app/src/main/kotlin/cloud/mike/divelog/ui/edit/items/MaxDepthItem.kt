package cloud.mike.divelog.ui.edit.items

import android.content.res.Configuration
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cloud.mike.divelog.R
import cloud.mike.divelog.data.dives.Dive
import cloud.mike.divelog.localization.isValidNumber
import cloud.mike.divelog.localization.primaryLocale
import cloud.mike.divelog.ui.DiveTheme
import cloud.mike.divelog.ui.common.form.FormIcon
import cloud.mike.divelog.ui.common.form.FormTextField
import cloud.mike.divelog.ui.edit.FormState
import cloud.mike.divelog.ui.edit.rememberFormState

@Composable
fun MaxDepthItem(
    formState: FormState,
    modifier: Modifier = Modifier,
) {
    val locale = primaryLocale
    Row(modifier = modifier) {
        FormIcon(Icons.Default.Height)
        FormTextField(
            modifier = Modifier.weight(1f).padding(end = 16.dp),
            singleLine = true,
            value = formState.maxDepthMeters,
            onValueChange = {
                if (it.isEmpty() || it.isValidNumber(locale)) formState.maxDepthMeters = it
            },
            placeholder = stringResource(R.string.edit_dive_placeholder_add_max_depth),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Done,
            ),
            visualTransformation = if (formState.maxDepthMeters.isBlank()) {
                VisualTransformation.None
            } else {
                SuffixTransformation(" m")
            },
        )
    }
}

private class SuffixTransformation(private val suffix: String) : VisualTransformation {
    override fun filter(text: AnnotatedString) = TransformedText(
        text = text + AnnotatedString(suffix),
        offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int) = offset
            override fun transformedToOriginal(offset: Int) = offset.coerceAtMost(text.length)
        },
    )
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun PreviewEmpty() {
    DiveTheme {
        MaxDepthItem(formState = rememberFormState(dive = null))
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun PreviewFilled() {
    DiveTheme {
        MaxDepthItem(formState = rememberFormState(dive = Dive.sample))
    }
}
