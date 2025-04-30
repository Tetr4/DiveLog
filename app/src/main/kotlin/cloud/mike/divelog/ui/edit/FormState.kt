package cloud.mike.divelog.ui.edit

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import cloud.mike.divelog.data.dives.Dive
import cloud.mike.divelog.localization.format
import cloud.mike.divelog.ui.common.DurationSaver
import cloud.mike.divelog.ui.common.LocalDateSaver
import cloud.mike.divelog.ui.common.LocalTimeSaver
import java.time.LocalDate
import java.time.LocalTime
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours

@Stable
@Suppress("LongParameterList") // Form has lots of fields
class FormState(
    number: MutableState<String>,
    startDate: MutableState<LocalDate?>,
    startTime: MutableState<LocalTime?>,
    duration: MutableState<Duration?>,
    location: MutableState<String>,
    maxDepthMeters: MutableState<String>,
    buddy: MutableState<String>,
    notes: MutableState<String>,
) {
    var number by number
    var startDate by startDate
    var startTime by startTime
    var duration by duration
    var location by location
    var maxDepthMeters by maxDepthMeters
    var buddy by buddy
    var notes by notes

    val canBeSaved
        get() = startDate != null && duration != null && number.isNotEmpty()
}

@Composable
fun rememberFormState(
    dive: Dive?,
    defaultNumber: Int? = null,
    defaultDate: LocalDate? = LocalDate.now(),
    defaultDuration: Duration? = 1.hours,
): FormState {
    val numberFormatted = (dive?.number ?: defaultNumber)?.format().orEmpty()
    val maxDepthMetersFormatted = dive?.maxDepthMeters?.format().orEmpty()
    return FormState(
        number = rememberSaveable(numberFormatted) {
            mutableStateOf(numberFormatted)
        },
        startDate = rememberSaveable(dive?.startDate, stateSaver = LocalDateSaver) {
            mutableStateOf(dive?.startDate ?: defaultDate)
        },
        startTime = rememberSaveable(dive?.startTime, stateSaver = LocalTimeSaver) {
            mutableStateOf(dive?.startTime)
        },
        duration = rememberSaveable(dive?.duration, stateSaver = DurationSaver) {
            mutableStateOf(dive?.duration ?: defaultDuration)
        },
        location = rememberSaveable(dive?.location) {
            mutableStateOf(dive?.location?.name.orEmpty())
        },
        maxDepthMeters = rememberSaveable(maxDepthMetersFormatted) {
            mutableStateOf(maxDepthMetersFormatted)
        },
        buddy = rememberSaveable(dive?.buddy) {
            mutableStateOf(dive?.buddy.orEmpty())
        },
        notes = rememberSaveable(dive?.notes) {
            mutableStateOf(dive?.notes.orEmpty())
        },
    )
}
