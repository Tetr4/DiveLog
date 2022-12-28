package cloud.mike.divelog

import cloud.mike.divelog.data.dataModule
import cloud.mike.divelog.ui.home.HomeViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val appModule = dataModule + module {
    viewModelOf(::HomeViewModel)
}