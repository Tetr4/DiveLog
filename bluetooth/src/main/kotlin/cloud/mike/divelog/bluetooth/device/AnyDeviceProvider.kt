package cloud.mike.divelog.bluetooth.device

import android.bluetooth.BluetoothDevice
import cloud.mike.divelog.bluetooth.pairing.PairingService
import io.reactivex.Observable
import java.util.Optional

/** Provides a random bonded device. */
class AnyDeviceProvider(private val pairingService: PairingService) : DeviceProvider {
    override val deviceStream: Observable<Optional<BluetoothDevice>> = pairingService.pairedDevicesStream
        .map { Optional.ofNullable(device) }

    override val device: BluetoothDevice?
        get() = pairingService.pairedDevices.firstOrNull()
}
