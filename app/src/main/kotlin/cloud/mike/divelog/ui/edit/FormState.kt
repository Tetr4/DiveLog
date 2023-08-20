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
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.time.Duration

@Stable
class FormState(
    startDate: MutableState<LocalDate?>,
    startTime: MutableState<LocalTime?>,
    diveTime: MutableState<Duration?>,
    notes: MutableState<String>,
) {
    var startDate by startDate
    var startTime by startTime
    var diveTime by diveTime
    var notes by notes

    val isValid
        get() = startDate != null && startTime != null && diveTime != null

    val start: LocalDateTime?
        get() {
            val date = startDate ?: return null
            val time = startTime ?: return null
            return LocalDateTime.of(date, time)
        }
}

@Composable
fun rememberFormState(dive: Dive?) = FormState(
    startDate = rememberSaveable(dive?.start, stateSaver = LocalDateSaver) {
        mutableStateOf(dive?.start?.toLocalDate())
    },
    startTime = rememberSaveable(dive?.start, stateSaver = LocalTimeSaver) {
        mutableStateOf(dive?.start?.toLocalTime())
    },
    diveTime = rememberSaveable(dive?.diveTime, stateSaver = DurationSaver) {
        mutableStateOf(dive?.diveTime)
    },
    notes = rememberSaveable(dive?.notes) {
        mutableStateOf(dive?.notes.orEmpty())
    },
)
