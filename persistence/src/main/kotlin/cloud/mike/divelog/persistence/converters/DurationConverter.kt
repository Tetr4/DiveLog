package cloud.mike.divelog.persistence.converters

import androidx.room.TypeConverter
import kotlin.time.Duration

class DurationConverter {
    @TypeConverter
    fun encode(value: Duration) = value.toIsoString()

    @TypeConverter
    fun decode(value: String) = Duration.parseIsoString(value)
}
