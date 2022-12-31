package cloud.mike.divelog.bluetooth.device

import android.bluetooth.BluetoothDevice
import io.reactivex.Observable
import java.util.Optional

interface DeviceProvider {
    val deviceStream: Observable<Optional<BluetoothDevice>>
    val device: BluetoothDevice?
}
