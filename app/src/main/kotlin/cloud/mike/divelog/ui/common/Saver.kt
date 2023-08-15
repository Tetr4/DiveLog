package cloud.mike.divelog.ui.common

import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import java.time.LocalDate
import java.time.LocalTime
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

data object LocalDateSaver : Saver<LocalDate?, String> {
    override fun restore(value: String): LocalDate = LocalDate.parse(value)
    override fun SaverScope.save(value: LocalDate?) = value?.toString()
}

data object LocalTimeSaver : Saver<LocalTime?, String> {
    override fun restore(value: String): LocalTime = LocalTime.parse(value)
    override fun SaverScope.save(value: LocalTime?) = value?.toString()
}

data object DurationSaver : Saver<Duration?, Long> {
    override fun restore(value: Long) = value.milliseconds
    override fun SaverScope.save(value: Duration?) = value?.inWholeMilliseconds
}
