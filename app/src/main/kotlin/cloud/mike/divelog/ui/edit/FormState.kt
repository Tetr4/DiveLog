package cloud.mike.divelog.ui.edit

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import cloud.mike.divelog.data.dives.Dive
import cloud.mike.divelog.ui.common.DurationSaver
import cloud.mike.divelog.ui.common.LocalDateSaver
import cloud.mike.divelog.ui.common.LocalTimeSaver
import java.time.LocalDate
import java.time.LocalTime
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours

@Stable
class FormState(
    startDate: MutableState<LocalDate?>,
    startTime: MutableState<LocalTime?>,
    duration: MutableState<Duration?>,
    location: MutableState<String>,
    notes: MutableState<String>,
) {
    var startDate by startDate
    var startTime by startTime
    var duration by duration
    var location by location
    var notes by notes

    val isValid
        get() = startDate != null && duration != null
}

@Composable
fun rememberFormState(
    dive: Dive?,
    defaultDate: LocalDate? = LocalDate.now(),
    defaultDuration: Duration? = 1.hours,
) = FormState(
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
    notes = rememberSaveable(dive?.notes) {
        mutableStateOf(dive?.notes.orEmpty())
    },
)
