package cloud.mike.divelog.bluetooth.device

import android.bluetooth.BluetoothDevice
import kotlinx.coroutines.flow.Flow

interface DeviceProvider {
    val deviceFlow: Flow<BluetoothDevice?>
    val device: BluetoothDevice?
}
