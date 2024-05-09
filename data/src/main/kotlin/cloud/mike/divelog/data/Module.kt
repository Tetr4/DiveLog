package cloud.mike.divelog.data

import cloud.mike.divelog.bluetooth.bluetoothModule
import cloud.mike.divelog.bluetooth.device.DeviceProvider
import cloud.mike.divelog.data.backup.BackupService
import cloud.mike.divelog.data.dives.DiveRepository
import cloud.mike.divelog.data.importer.DiveComputerProvider
import cloud.mike.divelog.data.importer.Importer
import cloud.mike.divelog.persistence.persistenceModule
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val dataModule = persistenceModule + bluetoothModule + module {
    singleOf(::DiveComputerProvider) bind DeviceProvider::class
    singleOf(::DiveRepository)
    singleOf(::Importer)
    singleOf(::BackupService)
}
