package cloud.mike.divelog.bluetooth.connector

import android.bluetooth.BluetoothDevice
import android.util.Log
import cloud.mike.divelog.bluetooth.device.DeviceProvider
import cloud.mike.divelog.bluetooth.pairing.BondState
import cloud.mike.divelog.bluetooth.pairing.bonding
import cloud.mike.divelog.bluetooth.precondition.PreconditionService
import cloud.mike.divelog.bluetooth.precondition.PreconditionState
import cloud.mike.divelog.bluetooth.utils.aliasOrNull
import com.polidea.rxandroidble2.RxBleClient
import com.polidea.rxandroidble2.Timeout
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

private val TAG = AutoConnector::class.java.simpleName
private const val CONNECTION_TIMEOUT_MS = 10_000L

class AutoConnector(
    private val bleClient: RxBleClient,
    private val preconditionService: PreconditionService,
    private val deviceProvider: DeviceProvider,
    appScope: CoroutineScope,
) {
    var connection: Connection? = null
        private set
    val connectionStateFlow = MutableStateFlow(ConnectionState.IDLE)
    val connectionState
        get() = connectionStateFlow.value
    val error = MutableSharedFlow<Exception>(extraBufferCapacity = 1)

    private var autoConnect = false
    private var connectionDisposable: Disposable? = null

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
            connectionDisposable != null -> Log.w(TAG, "Already connecting")
            else -> startConnection(device)
        }
    }

    private fun startConnection(device: BluetoothDevice) {
        val rxDevice = bleClient.getBleDevice(device.address)
        Log.i(TAG, "Start (${device.aliasOrNull})")
        connectionStateFlow.update { ConnectionState.CONNECTING }
        val timeout = Timeout(CONNECTION_TIMEOUT_MS, TimeUnit.MILLISECONDS)
        // TODO replace RX with flow?
        connectionDisposable = rxDevice
            .establishConnection(false, timeout)
            .subscribeOn(Schedulers.io())
            .retryWhen { errorStream ->
                errorStream
                    .doOnNext { Log.e(TAG, "Could not connect", it) }
                    .doOnNext { connectionStateFlow.update { ConnectionState.CONNECTING } }
                    .takeWhile { autoConnect }
                    .delay(2_000, TimeUnit.MILLISECONDS)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.i(TAG, "Connected (${device.aliasOrNull})")
                connection = Connection(it)
                connectionStateFlow.update { ConnectionState.CONNECTED }
            }, {
                stopConnection()
                showConnectionError(it)
            })
    }

    private fun stopConnection() {
        Log.i(TAG, "Stop")
        connectionDisposable?.dispose()
        connectionDisposable = null
        connection = null
        connectionStateFlow.update { ConnectionState.IDLE }
    }

    private fun showConnectionError(error: Throwable) {
        Log.e(TAG, "Connection error", error)
        this.error.tryEmit(ConnectionException(message = error.message, cause = error))
    }
}
