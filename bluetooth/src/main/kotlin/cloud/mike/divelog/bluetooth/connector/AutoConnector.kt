package cloud.mike.divelog.bluetooth.connector

import android.bluetooth.BluetoothDevice
import android.util.Log
import cloud.mike.divelog.bluetooth.device.DeviceProvider
import cloud.mike.divelog.bluetooth.pairing.BondState
import cloud.mike.divelog.bluetooth.pairing.bonding
import cloud.mike.divelog.bluetooth.precondition.PreconditionService
import cloud.mike.divelog.bluetooth.precondition.PreconditionState
import cloud.mike.divelog.bluetooth.utils.aliasOrNull
import com.polidea.rxandroidble3.RxBleClient
import com.polidea.rxandroidble3.RxBleConnection
import com.polidea.rxandroidble3.RxBleConnection.GATT_MTU_MAXIMUM
import com.polidea.rxandroidble3.RxBleConnection.GATT_MTU_MINIMUM
import com.polidea.rxandroidble3.Timeout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.rx3.asFlow
import kotlinx.coroutines.rx3.await
import java.util.concurrent.TimeUnit

private val TAG = AutoConnector::class.java.simpleName
private const val CONNECTION_TIMEOUT_MS = 10_000L

class AutoConnector(
    private val bleClient: RxBleClient,
    private val preconditionService: PreconditionService,
    private val deviceProvider: DeviceProvider,
    private val appScope: CoroutineScope,
) {
    var connection: Connection? = null
        private set
    val connectionStateFlow = MutableStateFlow(ConnectionState.IDLE)
    val connectionState
        get() = connectionStateFlow.value
    val error = MutableSharedFlow<Exception>(extraBufferCapacity = 1)

    private var autoConnect = false
    private var connectionJob: Job? = null

    init {
        Log.i(TAG, "Init")
        appScope.launch {
            preconditionService.preconditionFlow.collect { onStateChanged() }
        }
        appScope.launch {
            deviceProvider.deviceFlow.collect { onStateChanged() }
        }
    }

    fun connect() {
        Log.i(TAG, "Connect")
        autoConnect = true
        ensurePreconditions()
    }

    fun disconnect() {
        Log.i(TAG, "Disconnect")
        autoConnect = false
        stopConnection()
    }

    private fun onStateChanged() {
        val precondition = preconditionService.precondition
        val deviceName = deviceProvider.device?.aliasOrNull ?: "no device"
        Log.i(TAG, "State changed: $precondition - $deviceName")
        stopConnection()
        if (autoConnect) connect()
    }

    private fun ensurePreconditions() {
        when (val precondition = preconditionService.precondition) {
            PreconditionState.READY -> ensurePairing()
            else -> showConnectionError(ConnectionException("Preconditions not ready: $precondition"))
        }
    }

    private fun ensurePairing() {
        val device = deviceProvider.device
        if (device == null || device.bonding != BondState.BONDED) {
            showConnectionError(ConnectionException("No device bonded"))
        } else {
            ensureConnection(device)
        }
    }

    private fun ensureConnection(device: BluetoothDevice) {
        when {
            connection != null -> Log.w(TAG, "Already connected")
            connectionJob != null -> Log.w(TAG, "Already connecting")
            else -> startConnection(device)
        }
    }

    private fun startConnection(device: BluetoothDevice) {
        val rxDevice = bleClient.getBleDevice(device.address)
        Log.i(TAG, "Start (${device.aliasOrNull})")
        connectionStateFlow.update { ConnectionState.CONNECTING }
        val timeout = Timeout(CONNECTION_TIMEOUT_MS, TimeUnit.MILLISECONDS)
        connectionJob = appScope.launch {
            rxDevice
                .establishConnection(false, timeout)
                .asFlow()
                .retryWhen { cause, _ ->
                    Log.e(TAG, "Could not connect", cause)
                    connectionStateFlow.update { ConnectionState.CONNECTING }
                    delay(2_000)
                    autoConnect
                }
                .onEach { connection -> connection.tryRequestMaxMtu() }
                .catch {
                    stopConnection()
                    showConnectionError(it)
                }
                .collect {
                    Log.i(TAG, "Connected (${device.aliasOrNull})")
                    connection = Connection(it)
                    connectionStateFlow.update { ConnectionState.CONNECTED }
                }
        }
    }

    private fun stopConnection() {
        Log.i(TAG, "Stop")
        connectionJob?.cancel()
        connectionJob = null
        connection = null
        connectionStateFlow.update { ConnectionState.IDLE }
    }

    private fun showConnectionError(error: Throwable) {
        Log.e(TAG, "Connection error", error)
        this.error.tryEmit(ConnectionException(message = error.message, cause = error))
    }
}

private suspend fun RxBleConnection.tryRequestMaxMtu() {
    val newMtu = try {
        this.requestMtu(GATT_MTU_MAXIMUM).await()
    } catch (e: Exception) {
        Log.w(TAG, "Could not increase MTU", e)
    }
    when (newMtu) {
        GATT_MTU_MINIMUM -> Log.w(TAG, "Device does not support larger MTU")
        else -> Log.i(TAG, "Raised MTU to $newMtu")
    }
}
