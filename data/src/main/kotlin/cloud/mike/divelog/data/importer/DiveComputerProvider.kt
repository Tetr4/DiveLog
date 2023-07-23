package cloud.mike.divelog.data.importer

import android.bluetooth.BluetoothDevice
import cloud.mike.divelog.bluetooth.device.DeviceProvider
import cloud.mike.divelog.bluetooth.pairing.PairingService
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

internal class DiveComputerProvider(private val pairingService: PairingService) : DeviceProvider {

    override val deviceFlow = pairingService.pairedDevicesFlow
        .map { device }
        .distinctUntilChanged()

    override val device: BluetoothDevice?
        get() = pairingService.pairedDevices.find { it.isDiveComputer }
}
