package cloud.mike.divelog.data

import cloud.mike.divelog.data.dives.DiveRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val dataModule = module {
    singleOf(::DiveRepository)
}
