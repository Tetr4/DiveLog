package cloud.mike.divelog.ui.common.picker

import android.content.res.Configuration
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.utf16CodePoint
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import cloud.mike.divelog.R
import cloud.mike.divelog.ui.DiveTheme
import cloud.mike.divelog.ui.spacing

@Composable
fun DurationPicker(
    state: DurationPickerState,
    modifier: Modifier = Modifier,
) {
    val hoursFocusRequester = remember { FocusRequester() }
    val minutesFocusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        hoursFocusRequester.requestFocus()
    }

    Row(modifier = modifier) {
        Column {
            HoursField(
                modifier = Modifier.focusRequester(hoursFocusRequester),
                value = state.hoursText,
                onValueChange = { state.hoursText = it },
                onFocusMinutes = { minutesFocusRequester.requestFocus() },
            )
            Label(stringResource(R.string.duration_picker_label_hours))
        }
        Separator()
        Column {
            MinutesField(
                modifier = Modifier.focusRequester(minutesFocusRequester),
                value = state.minutesText,
                onValueChange = { state.minutesText = it },
                onFocusHours = { hoursFocusRequester.requestFocus() },
            )
            Label(stringResource(R.string.duration_picker_label_minutes))
        }
    }
}

@Composable
private fun HoursField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    onFocusMinutes: () -> Unit,
    modifier: Modifier,
) {
    val contentDescription = stringResource(R.string.duration_picker_label_hours)
    PickerField(
        modifier = modifier
            .semantics { this.contentDescription = contentDescription }
            .onKeyEvent { event ->
                // Always focus next field on key press
                if (event.isDigit) onFocusMinutes()
                false
            },
        value = value,
        onValueChange = { newValue ->
            when {
                !newValue.text.isDigitsOnly() -> Unit
                newValue.text.length > 1 -> {
                    val truncated = newValue.copy(text = newValue.text.take(1))
                    onValueChange(truncated)
                }
                else -> onValueChange(newValue)
            }
        },
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Next,
            keyboardType = KeyboardType.Number,
        ),
        keyboardActions = KeyboardActions(
            onNext = { onFocusMinutes() },
        ),
    )
}

@Composable
private fun MinutesField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    onFocusHours: () -> Unit,
    modifier: Modifier,
) {
    val contentDescription = stringResource(R.string.duration_picker_label_minutes)
    PickerField(
        modifier = modifier
            .semantics { this.contentDescription = contentDescription }
            .onPreviewKeyEvent { event ->
                // Focus previous field on delete key press on empty field
                val switchFocus = event.isDelete && value.selection.start == 0
                if (switchFocus) {
                    onFocusHours()
                }
                switchFocus
            },
        value = value,
        onValueChange = { newValue ->
            when {
                !newValue.text.isDigitsOnly() -> Unit
                newValue.text.length > 2 -> {
                    val truncated = newValue.copy(text = newValue.text.take(2))
                    onValueChange(truncated)
                }
                else -> onValueChange(newValue)
            }
        },
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Done,
            keyboardType = KeyboardType.Number,
        ),
    )
}

@Composable
private fun Separator() {
    Box(
        modifier = Modifier
            .size(24.dp, 72.0.dp)
            .clearAndSetSemantics {},
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = ":",
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.displayMedium,
        )
    }
}

@Composable
private fun Label(text: String) {
    Text(
        modifier = Modifier
            .padding(top = 7.dp)
            .clearAndSetSemantics {},
        text = text,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        style = MaterialTheme.typography.bodySmall,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PickerField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
) {
    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
        focusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
    )
    val interactionSource = remember { MutableInteractionSource() }

    // Can't use OutlinedTextField, because we need to remove contentPadding on the decoration
    // See TimePickerTextField in Material 3 TimePicker
    BasicTextField(
        modifier = modifier.size(96.dp, 72.dp),
        value = value,
        onValueChange = onValueChange,
        interactionSource = interactionSource,
        enabled = true,
        singleLine = true,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        textStyle = MaterialTheme.typography.displayMedium.copy(
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
        ),
        cursorBrush = Brush.verticalGradient(
            0.00f to Color.Transparent,
            0.10f to Color.Transparent,
            0.10f to MaterialTheme.colorScheme.primary,
            0.90f to MaterialTheme.colorScheme.primary,
            0.90f to Color.Transparent,
            1.00f to Color.Transparent,
        ),
    ) {
        OutlinedTextFieldDefaults.DecorationBox(
            value = value.text,
            visualTransformation = VisualTransformation.None,
            innerTextField = it,
            singleLine = true,
            colors = textFieldColors,
            enabled = true,
            interactionSource = interactionSource,
            contentPadding = PaddingValues(0.dp),
            container = {
                OutlinedTextFieldDefaults.ContainerBox(
                    enabled = true,
                    isError = false,
                    interactionSource = interactionSource,
                    shape = MaterialTheme.shapes.small,
                    colors = textFieldColors,
                )
            },
        )
    }
}

private val KeyEvent.isDigit
    get() = Character.isDigit(utf16CodePoint)

private val KeyEvent.isDelete
    get() = utf16CodePoint == 0

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun Preview() {
    DiveTheme {
        Surface {
            DurationPicker(
                modifier = Modifier.padding(MaterialTheme.spacing.dialogPadding),
                state = rememberDurationPickerState(),
            )
        }
    }
}
