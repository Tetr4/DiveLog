package cloud.mike.divelog.bluetooth.device

import android.bluetooth.BluetoothDevice
import cloud.mike.divelog.bluetooth.pairing.PairingService
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

/** Provides a random bonded device. */
class AnyDeviceProvider(private val pairingService: PairingService) : DeviceProvider {
    override val deviceFlow = pairingService.pairedDevicesFlow
        .map { device }
        .distinctUntilChanged()

    override val device: BluetoothDevice?
        get() = pairingService.pairedDevices.firstOrNull()
}
