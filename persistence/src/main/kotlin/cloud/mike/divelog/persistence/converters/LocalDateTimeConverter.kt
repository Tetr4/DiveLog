package cloud.mike.divelog.persistence.converters

import androidx.room.TypeConverter
import java.time.LocalDateTime

class LocalDateTimeConverter {
    @TypeConverter
    fun encode(value: LocalDateTime) = value.toString() //  ISO-8601

    @TypeConverter
    fun decode(value: String): LocalDateTime = LocalDateTime.parse(value)
}
