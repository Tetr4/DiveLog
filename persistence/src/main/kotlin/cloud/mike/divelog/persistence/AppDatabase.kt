package cloud.mike.divelog.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import cloud.mike.divelog.persistence.converters.DurationConverter
import cloud.mike.divelog.persistence.converters.IntArrayConverter
import cloud.mike.divelog.persistence.converters.LocalDateConverter
import cloud.mike.divelog.persistence.converters.LocalTimeConverter
import cloud.mike.divelog.persistence.converters.UuidConverter
import cloud.mike.divelog.persistence.dives.DiveDto
import cloud.mike.divelog.persistence.dives.DiveLocationDto
import cloud.mike.divelog.persistence.dives.DiveProfileDto
import cloud.mike.divelog.persistence.dives.DivesDao

@Database(
    entities = [
        DiveDto::class,
        DiveLocationDto::class,
        DiveProfileDto::class,
    ],
    version = 1,
)
@TypeConverters(
    LocalDateConverter::class,
    LocalTimeConverter::class,
    DurationConverter::class,
    IntArrayConverter::class,
    UuidConverter::class,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun divesDao(): DivesDao
}