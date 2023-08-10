package cloud.mike.divelog.persistence.converters

import androidx.room.TypeConverter
import java.util.UUID

class UuidConverter {
    @TypeConverter
    fun encode(value: UUID) = value.toString()

    @TypeConverter
    fun decode(value: String): UUID = UUID.fromString(value)
}
