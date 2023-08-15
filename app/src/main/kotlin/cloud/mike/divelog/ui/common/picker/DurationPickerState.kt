package cloud.mike.divelog.ui.common.picker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import cloud.mike.divelog.localization.primaryLocale
import java.text.NumberFormat
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

@Stable
class DurationPickerState(
    hoursText: MutableState<TextFieldValue>,
    minutesText: MutableState<TextFieldValue>,
) {
    var hoursText by hoursText
    var minutesText by minutesText

    val duration: Duration
        get() {
            val hours = hoursText.text.toIntOrNull() ?: 0
            val minutes = minutesText.text.toIntOrNull() ?: 0
            return hours.hours + minutes.minutes
        }
}

@Composable
fun rememberDurationPickerState(initial: Duration = Duration.ZERO): DurationPickerState {
    val initialHours = initial.inWholeHours
    val initialMinutes = initial.inWholeMinutes - initial.inWholeHours * 60
    val initialHoursFormatted = initialHours.formatDurationField(digits = 1)
    val initialMinutesFormatted = initialMinutes.formatDurationField(digits = 2)
    return DurationPickerState(
        hoursText = rememberSaveable(stateSaver = TextFieldValue.Saver) {
            mutableStateOf(TextFieldValue(initialHoursFormatted))
        },
        minutesText = rememberSaveable(stateSaver = TextFieldValue.Saver) {
            mutableStateOf(TextFieldValue(initialMinutesFormatted))
        },
    )
}

@Composable
@ReadOnlyComposable
private fun Long.formatDurationField(digits: Int): String = NumberFormat
    .getIntegerInstance(primaryLocale)
    .apply {
        isGroupingUsed = false
        minimumIntegerDigits = digits
    }
    .format(this)