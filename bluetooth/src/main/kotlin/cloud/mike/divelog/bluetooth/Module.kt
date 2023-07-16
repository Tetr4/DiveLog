package cloud.mike.divelog.bluetooth

import cloud.mike.divelog.bluetooth.connector.AutoConnector
import cloud.mike.divelog.bluetooth.pairing.PairingService
import cloud.mike.divelog.bluetooth.precondition.PreconditionService
import com.polidea.rxandroidble2.RxBleClient
import kotlinx.coroutines.MainScope
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val bluetoothModule = module {
    singleOf(RxBleClient::create)
    singleOf(::PairingService)
    singleOf(::PreconditionService)
    singleOf(::AutoConnector)

    // Main Scope (active until the app's process is killed)
    single { MainScope() }
}
