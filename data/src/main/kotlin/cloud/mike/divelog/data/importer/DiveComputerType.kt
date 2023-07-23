package cloud.mike.divelog.data.importer

import android.bluetooth.BluetoothDevice
import cloud.mike.divelog.bluetooth.utils.nameOrNull
import cloud.mike.divelog.data.importer.DiveComputerType.OSTC

internal enum class DiveComputerType {
    OSTC,
}

internal val BluetoothDevice.isDiveComputer
    get() = diveComputerType != null

internal val BluetoothDevice.diveComputerType: DiveComputerType?
    get() {
        val name = nameOrNull.orEmpty()
        return when {
            name.startsWith("OSTC") -> OSTC
            else -> null
        }
    }
