package cloud.mike.divelog.persistence.converters

import androidx.room.TypeConverter
import java.time.LocalDate

class LocalDateConverter {
    @TypeConverter
    fun encode(value: LocalDate) = value.toString() //  ISO-8601

    @TypeConverter
    fun decode(value: String): LocalDate = LocalDate.parse(value)
}
