package cloud.mike.divelog.persistence.converters

import androidx.room.TypeConverter
import java.time.LocalTime

class LocalTimeConverter {
    @TypeConverter
    fun encode(value: LocalTime) = value.toString() //  ISO-8601

    @TypeConverter
    fun decode(value: String): LocalTime = LocalTime.parse(value)
}
