package cloud.mike.divelog.data.importer

import android.bluetooth.BluetoothDevice
import cloud.mike.divelog.bluetooth.utils.nameOrNull
import cloud.mike.divelog.data.importer.DiveComputerType.OSTC
import cloud.mike.divelog.data.importer.DiveComputerType.SHEARWATER

internal enum class DiveComputerType {
    OSTC,
    SHEARWATER,
}

internal val BluetoothDevice.diveComputerType: DiveComputerType?
    get() {
        val name = nameOrNull.orEmpty()
        return when {
            name.startsWith("OSTC") -> OSTC
            name.startsWith("Perdix") -> SHEARWATER
            else -> null
        }
    }

internal val BluetoothDevice.isDiveComputer
    get() = diveComputerType != null
