package cloud.mike.divelog.localization

import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.ParseException
import java.util.Locale

fun String.isValidNumber(locale: Locale) = toNumberOrNull(locale) != null

fun String.isValidInteger(locale: Locale): Boolean {
    val decimalFormat = DecimalFormat.getInstance(locale) as DecimalFormat
    val decimalSeparator = decimalFormat.decimalFormatSymbols.decimalSeparator
    return toIntegerOrNull(locale) != null && this.none { it == decimalSeparator }
}

fun String.toIntegerOrNull(locale: Locale): Int? = try {
    NumberFormat.getIntegerInstance(locale).parse(this)?.toInt()
} catch (e: ParseException) {
    null
}

fun String.toNumberOrNull(locale: Locale): Number? = try {
    NumberFormat.getInstance(locale).parse(this)
} catch (e: ParseException) {
    null
}
