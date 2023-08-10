package cloud.mike.divelog.persistence

import androidx.room.Room
import org.koin.dsl.module

val persistenceModule = module {
    single {
        Room.databaseBuilder(
            context = get(),
            klass = AppDatabase::class.java,
            name = "database",
        ).build()
    }
    single { get<AppDatabase>().divesDao() }
}
