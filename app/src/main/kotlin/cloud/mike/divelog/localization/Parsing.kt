package cloud.mike.divelog.localization

import java.text.NumberFormat
import java.text.ParseException
import java.util.Locale

fun String.isValidNumber(locale: Locale) = toNumberOrNull(locale) != null

fun String.toNumberOrNull(locale: Locale): Number? = try {
    NumberFormat.getInstance(locale).parse(this)?.toFloat()
} catch (e: ParseException) {
    null
}
