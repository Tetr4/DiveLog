package cloud.mike.divelog.localization

import android.icu.text.MeasureFormat
import android.icu.text.MeasureFormat.FormatWidth
import android.icu.util.LocaleData
import android.icu.util.Measure
import android.icu.util.MeasureUnit
import android.icu.util.ULocale
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalContext
import androidx.core.text.util.LocalePreferences
import java.text.NumberFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
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
    val imperial = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        LocaleData.getMeasurementSystem(ULocale.forLocale(primaryLocale)) == LocaleData.MeasurementSystem.US
    } else {
        primaryLocale.country == "US"
    }
    val converted = if (imperial) this.toDouble() * 3.28084 else this.toDouble()
    val measureUnit = if (imperial) MeasureUnit.FOOT else MeasureUnit.METER
    return MeasureFormat
        .getInstance(primaryLocale, FormatWidth.SHORT)
        .format(Measure(converted, measureUnit))
}

@Composable
@ReadOnlyComposable
fun Number.formatTemperatureCelsius(): String {
    val temperatureUnit = LocalePreferences.getTemperatureUnit(primaryLocale)
    val converted = when (temperatureUnit) {
        LocalePreferences.TemperatureUnit.FAHRENHEIT -> (this.toDouble() * 9 / 5) + 32
        LocalePreferences.TemperatureUnit.KELVIN -> this.toDouble() + 273.15
        else -> this.toDouble() // Celsius
    }
    val measureUnit = when (temperatureUnit) {
        LocalePreferences.TemperatureUnit.FAHRENHEIT -> MeasureUnit.FAHRENHEIT
        LocalePreferences.TemperatureUnit.KELVIN -> MeasureUnit.KELVIN
        else -> MeasureUnit.CELSIUS
    }
    return MeasureFormat
        .getInstance(primaryLocale, FormatWidth.SHORT)
        .format(Measure(converted, measureUnit))
}

@Composable
@ReadOnlyComposable
fun LocalDate.format(style: FormatStyle = FormatStyle.MEDIUM): String = DateTimeFormatter
    .ofLocalizedDate(style)
    .withLocale(primaryLocale)
    .format(this)

@Composable
@ReadOnlyComposable
fun LocalTime.format(style: FormatStyle = FormatStyle.MEDIUM): String = DateTimeFormatter
    .ofLocalizedTime(style)
    .withLocale(primaryLocale)
    .format(this)

@Composable
@ReadOnlyComposable
fun LocalDateTime.format(style: FormatStyle = FormatStyle.MEDIUM): String = DateTimeFormatter
    .ofLocalizedDateTime(style)
    .withLocale(primaryLocale)
    .format(this)

@Composable
@ReadOnlyComposable
fun Duration.format() = "$inWholeMinutes min"

val primaryLocale: Locale
    @Composable
    @ReadOnlyComposable
    get() {
        // Never use Locale.getDefault(), as it does not support runtime changes
        val context = LocalContext.current
        return context.resources.configuration.locales.get(0)
    }
