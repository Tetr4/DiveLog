package cloud.mike.divelog.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import cloud.mike.divelog.persistence.converters.DurationConverter
import cloud.mike.divelog.persistence.converters.IntArrayConverter
import cloud.mike.divelog.persistence.converters.LocalDateTimeConverter
import cloud.mike.divelog.persistence.converters.UuidConverter
import cloud.mike.divelog.persistence.dives.DepthProfileDto
import cloud.mike.divelog.persistence.dives.DiveDto
import cloud.mike.divelog.persistence.dives.DiveSpotDto
import cloud.mike.divelog.persistence.dives.DivesDao

@Database(
    entities = [
        DiveDto::class,
        DiveSpotDto::class,
        DepthProfileDto::class,
    ],
    version = 1,
)
@TypeConverters(
    LocalDateTimeConverter::class,
    DurationConverter::class,
    IntArrayConverter::class,
    UuidConverter::class,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun divesDao(): DivesDao
}