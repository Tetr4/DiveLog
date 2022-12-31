package cloud.mike.divelog.bluetooth.connector

import android.bluetooth.BluetoothDevice
import android.util.Log
import cloud.mike.divelog.bluetooth.device.DeviceProvider
import cloud.mike.divelog.bluetooth.pairing.BondState
import cloud.mike.divelog.bluetooth.pairing.bonding
import cloud.mike.divelog.bluetooth.precondition.PreconditionService
import cloud.mike.divelog.bluetooth.precondition.PreconditionState
import cloud.mike.divelog.bluetooth.utils.aliasOrName
import cloud.mike.divelog.bluetooth.utils.subscribeForever
import com.polidea.rxandroidble2.RxBleClient
import com.polidea.rxandroidble2.Timeout
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit

private val TAG = AutoConnector::class.java.simpleName
private const val CONNECTION_TIMEOUT_MS = 10_000L

class AutoConnector(
    private val bleClient: RxBleClient,
    private val preconditionService: PreconditionService,
    private val deviceProvider: DeviceProvider,
) {
    val connectionStateStream = BehaviorSubject.createDefault(ConnectionState.IDLE)
    val connectionState
        get() = connectionStateStream.value!! // default value, so this can not be null
    val error = PublishSubject.create<Exception>()
    var connection: Connection? = null
        private set

    private var autoConnect = false
    private var connectionDisposable: Disposable? = null

    init {
        Log.i(TAG, "Init")
        preconditionService.preconditionStream.subscribeForever { onStateChanged() }
        deviceProvider.deviceStream.distinctUntilChanged().subscribeForever { onStateChanged() }
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
        val deviceName = deviceProvider.device?.aliasOrName ?: "no device"
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
        Log.i(TAG, "Start (${device.aliasOrName})")
        connectionStateStream.onNext(ConnectionState.CONNECTING)
        val timeout = Timeout(CONNECTION_TIMEOUT_MS, TimeUnit.MILLISECONDS)
        connectionDisposable = rxDevice.establishConnection(false, timeout).subscribeOn(Schedulers.io())
            .retryWhen { errorStream ->
                errorStream
                    .doOnNext { Log.e(TAG, "Could not connect", it) }
                    .doOnNext { connectionStateStream.onNext(ConnectionState.CONNECTING) }
                    .takeWhile { autoConnect }
                    .delay(2_000, TimeUnit.MILLISECONDS)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.i(TAG, "Connected (${device.aliasOrName})")
                connection = Connection(it)
                connectionStateStream.onNext(ConnectionState.CONNECTED)
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
        connectionStateStream.onNext(ConnectionState.IDLE)
    }

    private fun showConnectionError(error: Throwable) {
        Log.e(TAG, "Connection error", error)
        this.error.onNext(ConnectionException(message = error.message, cause = error))
    }
}
