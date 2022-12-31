package cloud.mike.divelog.data

import cloud.mike.divelog.bluetooth.bluetoothModule
import cloud.mike.divelog.bluetooth.device.DeviceProvider
import cloud.mike.divelog.data.device.OstcDeviceProvider
import cloud.mike.divelog.data.dives.DiveRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val dataModule = bluetoothModule + module {
    singleOf(::OstcDeviceProvider) bind DeviceProvider::class
    singleOf(::DiveRepository)
}
