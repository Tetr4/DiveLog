package cloud.mike.divelog.bluetooth.utils

import android.bluetooth.BluetoothDevice
import android.os.Build
import android.util.Log

private val TAG = BluetoothDevice::class.java.simpleName

val BluetoothDevice.aliasOrName
    get() = try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) alias else name
    } catch (e: SecurityException) {
        Log.w(TAG, "Missing permission to access device name", e)
        null
    }
