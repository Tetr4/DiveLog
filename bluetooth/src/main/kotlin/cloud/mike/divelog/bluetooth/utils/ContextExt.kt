package cloud.mike.divelog.bluetooth.utils

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context

val Context.bluetoothAdapter: BluetoothAdapter?
    get() {
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        return bluetoothManager.adapter
    }
