package cloud.mike.divelog.data.device

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.util.Log
import cloud.mike.divelog.bluetooth.device.DeviceProvider
import cloud.mike.divelog.bluetooth.pairing.PairingService
import io.reactivex.Observable
import java.util.Optional

private val TAG = OstcDeviceProvider::class.java.simpleName

class OstcDeviceProvider(private val pairingService: PairingService) : DeviceProvider {

    override val deviceStream: Observable<Optional<BluetoothDevice>> =
        pairingService.pairedDevicesStream.map { pairedDevices ->
            val device = pairedDevices.find { it.isOstc }
            Optional.ofNullable(device)
        }

    override val device: BluetoothDevice?
        get() = pairingService.pairedDevices.find { it.isOstc }
}

private val BluetoothDevice.isOstc
    @SuppressLint("MissingPermission")
    get() = try {
        name != null && name.startsWith("OSTC")
    } catch (e: SecurityException) {
        Log.w(TAG, "Missing permission to access device name", e)
        false
    }
