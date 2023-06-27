package cloud.mike.divelog.bluetooth.pairing

import android.bluetooth.BluetoothDevice
import android.util.Log

private val TAG = BondState::class.java.simpleName

enum class BondState(val extra: Int) {
    NONE(BluetoothDevice.BOND_NONE),
    BONDED(BluetoothDevice.BOND_BONDED),
    BONDING(BluetoothDevice.BOND_BONDING),
    UNKNOWN(0),
    ;

    companion object {
        fun fromExtra(extra: Int) = values().find { it.extra == extra } ?: UNKNOWN
    }
}

val BluetoothDevice.bonding
    get() = try {
        BondState.fromExtra(this.bondState)
    } catch (e: SecurityException) {
        Log.w(TAG, "Missing permission to access bond state", e)
        BondState.UNKNOWN
    }
