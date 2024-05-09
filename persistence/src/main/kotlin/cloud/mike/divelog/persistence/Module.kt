package cloud.mike.divelog.persistence

import androidx.room.Room
import androidx.room.RoomDatabase
import org.koin.dsl.module

val persistenceModule = module {
    single {
        Room.databaseBuilder(
            context = get(),
            klass = AppDatabase::class.java,
            name = AppDatabase.FILE_NAME,
        )
            // Disable Write-Ahead Logging, so backups only require a single file.
            // This is okay, as concurrent reading and writing is not a bottleneck. Alternatively we could force a
            // checkpoint before creating or restoring a backup: https://stackoverflow.com/a/51560124
            .setJournalMode(RoomDatabase.JournalMode.TRUNCATE)
            .build()
    }
    single { get<AppDatabase>().divesDao() }
}
