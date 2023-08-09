package cloud.mike.divelog.data.importer

import cloud.mike.divelog.bluetooth.connector.AutoConnector
import cloud.mike.divelog.bluetooth.connector.ConnectionState.CONNECTED
import cloud.mike.divelog.bluetooth.connector.ConnectionState.CONNECTING
import cloud.mike.divelog.bluetooth.connector.ConnectionState.IDLE
import cloud.mike.divelog.bluetooth.device.DeviceProvider
import cloud.mike.divelog.bluetooth.precondition.PreconditionService
import cloud.mike.divelog.bluetooth.precondition.PreconditionState.BLUETOOTH_CONNECTION_PERMISSION_NOT_GRANTED
import cloud.mike.divelog.bluetooth.precondition.PreconditionState.BLUETOOTH_NOT_AVAILABLE
import cloud.mike.divelog.bluetooth.precondition.PreconditionState.BLUETOOTH_NOT_ENABLED
import cloud.mike.divelog.bluetooth.precondition.PreconditionState.READY
import cloud.mike.divelog.bluetooth.utils.aliasOrNull
import cloud.mike.divelog.bluetooth.utils.nameOrNull
import cloud.mike.divelog.data.importer.ImportConnectionState.BluetoothNotAvailable
import cloud.mike.divelog.data.importer.ImportConnectionState.BluetoothNotEnabled
import cloud.mike.divelog.data.importer.ImportConnectionState.Connected
import cloud.mike.divelog.data.importer.ImportConnectionState.Connecting
import cloud.mike.divelog.data.importer.ImportConnectionState.ConnectionPermissionNotGranted
import cloud.mike.divelog.data.importer.ImportConnectionState.NotConnected
import cloud.mike.divelog.data.importer.ImportConnectionState.NotPaired
import cloud.mike.divelog.data.importer.ostc.OstcConnection
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onStart

sealed interface ImportConnectionState {
    val deviceName: String?
        get() = null

    data object BluetoothNotAvailable : ImportConnectionState
    data object ConnectionPermissionNotGranted : ImportConnectionState
    data object BluetoothNotEnabled : ImportConnectionState
    data object NotPaired : ImportConnectionState
    data class NotConnected(override val deviceName: String?) : ImportConnectionState
    data class Connecting(override val deviceName: String?) : ImportConnectionState
    data class Connected(override val deviceName: String?) : ImportConnectionState
}

class Importer(
    private val deviceProvider: DeviceProvider,
    private val preconditionService: PreconditionService,
    private val autoConnector: AutoConnector,
) {
    val connectionState
        get() = when (preconditionService.precondition) {
            BLUETOOTH_NOT_AVAILABLE -> BluetoothNotAvailable
            BLUETOOTH_CONNECTION_PERMISSION_NOT_GRANTED -> ConnectionPermissionNotGranted
            BLUETOOTH_NOT_ENABLED -> BluetoothNotEnabled
            READY -> when (val device = deviceProvider.device) {
                null -> NotPaired
                else -> when (autoConnector.connectionState) {
                    IDLE -> NotConnected(device.aliasOrNull)
                    CONNECTING -> Connecting(device.aliasOrNull)
                    CONNECTED -> Connected(device.aliasOrNull)
                }
            }
        }

    val connectionStateFlow = merge(
        deviceProvider.deviceFlow.onStart { emit(deviceProvider.device) },
        preconditionService.preconditionFlow.onStart { emit(preconditionService.precondition) },
        autoConnector.connectionStateFlow.onStart { emit(autoConnector.connectionState) },
    )
        .map { connectionState }
        .distinctUntilChanged()

    val errorFlow = autoConnector.error

    val connection: ImportConnection?
        get() {
            val bleConnection = autoConnector.connection ?: return null
            val device = deviceProvider.device ?: return null
            return when (device.diveComputerType) {
                DiveComputerType.OSTC -> OstcConnection(bleConnection, onDisconnect = ::disconnect)
                null -> error("Unsupported device ${device.nameOrNull}")
            }
        }

    fun connect() = autoConnector.connect()

    fun disconnect() = autoConnector.disconnect()
}
