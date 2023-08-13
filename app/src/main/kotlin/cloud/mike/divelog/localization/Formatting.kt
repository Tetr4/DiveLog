package cloud.mike.divelog.localization

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalContext
import java.text.NumberFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale
import kotlin.time.Duration

@Composable
@ReadOnlyComposable
fun Number.format(): String = NumberFormat.getInstance(primaryLocale).format(this)

@Composable
@ReadOnlyComposable
fun Number.formatDiveNumber(): String = "#${this.format()}"

@Composable
@ReadOnlyComposable
fun Number.formatDepthMeters(): String {
    val numberFormat = NumberFormat.getInstance(primaryLocale).apply {
        maximumFractionDigits = 2
    }
    return numberFormat.format(this) + " m"
}

@Composable
@ReadOnlyComposable
fun LocalDateTime.format(style: FormatStyle = FormatStyle.MEDIUM): String = this.format(
    DateTimeFormatter
        .ofLocalizedDate(style)
        .withLocale(primaryLocale),
)

@Composable
@ReadOnlyComposable
fun LocalDate.format(style: FormatStyle = FormatStyle.MEDIUM): String = this.format(
    DateTimeFormatter
        .ofLocalizedDate(style)
        .withLocale(primaryLocale),
)

@Composable
@ReadOnlyComposable
fun Duration.format() = "$inWholeMinutes min"

private val primaryLocale: Locale
    @Composable
    @ReadOnlyComposable
    get() {
        // Never use Locale.getDefault(), as it does not support runtime changes
        val context = LocalContext.current
        return context.resources.configuration.locales.get(0)
    }
