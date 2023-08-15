package cloud.mike.divelog.ui.create

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
) {
    var startDate by startDate
    var startTime by startTime
    var diveTime by diveTime

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
fun rememberFormState() = FormState(
    startDate = rememberSaveable(stateSaver = LocalDateSaver) { mutableStateOf(null) },
    startTime = rememberSaveable(stateSaver = LocalTimeSaver) { mutableStateOf(null) },
    diveTime = rememberSaveable(stateSaver = DurationSaver) { mutableStateOf(null) },
)
