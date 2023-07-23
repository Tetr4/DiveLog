package cloud.mike.divelog.data.importer

import android.util.Log
import cloud.mike.divelog.bluetooth.connector.AutoConnector
import cloud.mike.divelog.bluetooth.connector.Connection
import cloud.mike.divelog.bluetooth.connector.ConnectionState.CONNECTED
import cloud.mike.divelog.bluetooth.connector.ConnectionState.CONNECTING
import cloud.mike.divelog.bluetooth.connector.ConnectionState.IDLE
import cloud.mike.divelog.bluetooth.device.DeviceProvider
import cloud.mike.divelog.bluetooth.precondition.PreconditionService
import cloud.mike.divelog.bluetooth.precondition.PreconditionState.BLUETOOTH_CONNECTION_PERMISSION_NOT_GRANTED
import cloud.mike.divelog.bluetooth.precondition.PreconditionState.BLUETOOTH_NOT_AVAILABLE
import cloud.mike.divelog.bluetooth.precondition.PreconditionState.BLUETOOTH_NOT_ENABLED
import cloud.mike.divelog.bluetooth.precondition.PreconditionState.READY
import cloud.mike.divelog.bluetooth.utils.aliasOrName
import cloud.mike.divelog.data.importer.DeviceConnectionState.BluetoothNotAvailable
import cloud.mike.divelog.data.importer.DeviceConnectionState.BluetoothNotEnabled
import cloud.mike.divelog.data.importer.DeviceConnectionState.Connected
import cloud.mike.divelog.data.importer.DeviceConnectionState.Connecting
import cloud.mike.divelog.data.importer.DeviceConnectionState.ConnectionPermissionNotGranted
import cloud.mike.divelog.data.importer.DeviceConnectionState.NotConnected
import cloud.mike.divelog.data.importer.DeviceConnectionState.NotPaired
import cloud.mike.divelog.data.importer.frames.CompactHeaderFrame
import cloud.mike.divelog.data.importer.frames.DiveProfileFrame
import cloud.mike.divelog.data.importer.frames.FullHeaderFrame
import cloud.mike.divelog.data.importer.frames.parseCompactHeaders
import cloud.mike.divelog.data.importer.frames.parseProfile
import cloud.mike.divelog.data.logging.TAG
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.runningReduce
import kotlinx.coroutines.flow.withIndex
import kotlinx.coroutines.launch
import java.util.UUID

private val UART_DATA_RX = UUID.fromString("00000001-0000-1000-8000-008025000000") // WRITE - NO RESPONSE
private val UART_DATA_TX = UUID.fromString("00000002-0000-1000-8000-008025000000") // NOTIFY
private val UART_CREDITS_RX = UUID.fromString("00000003-0000-1000-8000-008025000000") // WRITE

private const val MAX_CREDITS = 254
private const val MIN_CREDITS = 32

sealed interface DeviceConnectionState {
    val deviceName: String?
        get() = null

    object BluetoothNotAvailable : DeviceConnectionState
    object ConnectionPermissionNotGranted : DeviceConnectionState
    object BluetoothNotEnabled : DeviceConnectionState
    object NotPaired : DeviceConnectionState
    data class NotConnected(override val deviceName: String?) : DeviceConnectionState
    data class Connecting(override val deviceName: String?) : DeviceConnectionState
    data class Connected(override val deviceName: String?) : DeviceConnectionState
}

class Importer(
    private val deviceProvider: DeviceProvider,
    private val preconditionService: PreconditionService,
    private val autoConnector: AutoConnector,
    appScope: CoroutineScope,
) {
    val state
        get() = when (preconditionService.precondition) {
            BLUETOOTH_NOT_AVAILABLE -> BluetoothNotAvailable
            BLUETOOTH_CONNECTION_PERMISSION_NOT_GRANTED -> ConnectionPermissionNotGranted
            BLUETOOTH_NOT_ENABLED -> BluetoothNotEnabled
            READY -> when (val device = deviceProvider.device) {
                null -> NotPaired
                else -> when (autoConnector.connectionState) {
                    IDLE -> NotConnected(device.aliasOrName)
                    CONNECTING -> Connecting(device.aliasOrName)
                    CONNECTED -> Connected(device.aliasOrName)
                }
            }
        }

    val stateFlow = merge(
        deviceProvider.deviceFlow.onStart { emit(deviceProvider.device) },
        preconditionService.preconditionFlow.onStart { emit(preconditionService.precondition) },
        autoConnector.connectionStateFlow.onStart { emit(autoConnector.connectionState) },
    ).map { state }

    val errorFlow = autoConnector.error

    private var uartCredits = 0

    init {
        appScope.launch {
            autoConnector.connectionStateFlow.collect { uartCredits = 0 }
        }
    }

    fun connect() = autoConnector.connect()

    fun disconnect() = autoConnector.disconnect()

    suspend fun downloadProfiles(
        onProgress: suspend (Float) -> Unit,
        filterHeaders: (CompactHeaderFrame) -> Boolean,
    ): List<DiveProfileFrame> {
        val connection = autoConnector.connection ?: error("Not connected")
        connection.startCommunication()
        Log.i(TAG, "Fetching headers")
        val headers = connection.getCompactHeaders().filter(filterHeaders)
        val profiles = headers.mapIndexed { index, header ->
            val progress = index.toFloat() / headers.size.toFloat()
            onProgress(progress)
            Log.i(TAG, "Fetching profile #${header.profileNumber} ($progress%)")
            connection.getDiveProfile(
                profileNumber = header.profileNumber,
                profileSize = header.profileSize,
            )
        }
        connection.exitCommunication()
        return profiles
    }

    private suspend fun Connection.startCommunication() = sendCommand(
        command = 0xBB,
        responseLengthBytes = 0,
        checkStopByte = true, // will not echo if already in download mode, so we check the stop byte
    )

    private suspend fun Connection.exitCommunication() = sendByte(0xFF) // this kills connection, so no command echo

    private suspend fun Connection.getCompactHeaders() = sendCommand(
        command = 0x6D,
        responseLengthBytes = 4096,
    ).parseCompactHeaders()

    private suspend fun Connection.getDiveProfile(
        profileNumber: Int,
        profileSize: Int,
    ) = sendCommand(
        command = 0x66,
        responseLengthBytes = FullHeaderFrame.SIZE_BYTES + profileSize - 3,
        commandParameters = byteArrayOf(profileNumber.toByte()),
    ).parseProfile()

    private suspend fun Connection.ensureCredits() {
        if (uartCredits <= MIN_CREDITS) {
            sendCredits(MAX_CREDITS - uartCredits)
            uartCredits += (MAX_CREDITS - uartCredits)
        }
    }

    /**
     * This is an implementation of the OSTC transfer protocol.
     *
     * Workflow:
     * 1. Send credits to [UART_CREDITS_RX]
     * 2. Set up [UART_DATA_TX] notification
     * 3. Send [command] to [UART_DATA_RX]
     * 4. Receive command echo via [UART_DATA_TX]
     * 5. (Optional: Send [commandParameters] via [UART_DATA_RX]
     * 6. Receive response payload (multiple BLE messages) and ensure UART credits
     * 7. Receive stop byte (0x4D)
     */
    private suspend fun Connection.sendCommand(
        command: Int,
        responseLengthBytes: Int,
        checkStopByte: Boolean = false,
        commandParameters: ByteArray? = null,
    ): ByteArray {
        ensureCredits()
        val combinedResponse = subscribeData(onSubscribed = { sendByte(command) })
            .onEach { uartCredits--; ensureCredits() } // Send payload after command echo response
            .onFirst { commandParameters?.let { sendData(it) } } // Combine message payloads
            .runningReduce { accumulator, value -> accumulator + value }
            // Stop when combined response (minus echo and stop byte) has expected length
            .first { (it.size - 2 >= responseLengthBytes) || (checkStopByte && it.isStopByte) }
        return combinedResponse.drop(1).dropLast(1).toByteArray() // Drop echo and stop byte
    }
}

private suspend fun Connection.sendByte(data: Int) = sendData(byteArrayOf(data.toByte()))
private suspend fun Connection.sendData(bytes: ByteArray) = write(UART_DATA_RX, bytes)
private suspend fun Connection.sendCredits(credits: Int = 254) = write(UART_CREDITS_RX, byteArrayOf(credits.toByte()))
private fun Connection.subscribeData(onSubscribed: suspend () -> Unit) = setupNotification(UART_DATA_TX, onSubscribed)

private fun <T> Flow<T>.onFirst(function: suspend (value: T) -> Unit) =
    withIndex().onEach { (index, value) -> if (index == 0) function(value) }.map { it.value }

private val ByteArray.isStopByte: Boolean
    get() = size == 1 && first() == 0x4D.toByte()
