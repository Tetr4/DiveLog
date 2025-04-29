package cloud.mike.divelog

import cloud.mike.divelog.data.dataModule
import cloud.mike.divelog.localization.errors.ErrorService
import cloud.mike.divelog.ui.backup.BackupViewModel
import cloud.mike.divelog.ui.detail.DetailViewModel
import cloud.mike.divelog.ui.edit.EditViewModel
import cloud.mike.divelog.ui.home.HomeViewModel
import cloud.mike.divelog.ui.imports.ImportViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = dataModule + module {
    singleOf(::ErrorService)
    viewModelOf(::HomeViewModel)
    viewModelOf(::DetailViewModel)
    viewModelOf(::ImportViewModel)
    viewModelOf(::EditViewModel)
    viewModelOf(::BackupViewModel)
}
