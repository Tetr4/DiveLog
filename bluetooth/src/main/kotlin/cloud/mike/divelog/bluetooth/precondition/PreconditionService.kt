package cloud.mike.divelog.bluetooth.precondition

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import cloud.mike.divelog.bluetooth.precondition.PreconditionState.BLUETOOTH_CONNECTION_PERMISSION_NOT_GRANTED
import cloud.mike.divelog.bluetooth.precondition.PreconditionState.BLUETOOTH_NOT_AVAILABLE
import cloud.mike.divelog.bluetooth.precondition.PreconditionState.BLUETOOTH_NOT_ENABLED
import cloud.mike.divelog.bluetooth.precondition.PreconditionState.READY
import cloud.mike.divelog.bluetooth.utils.bluetoothAdapter
import com.polidea.rxandroidble2.RxBleClient
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit

private val TAG = PreconditionService::class.java.simpleName

/**
 * Service for resolving bluetooth LE scanning or connecting preconditions.
 * @see [PreconditionState]
 */
class PreconditionService(
    private val context: Context,
    private val bleClient: RxBleClient,
) {
    private val bluetoothAvailable
        get() = context.bluetoothAdapter != null

    private val bluetoothEnabled
        get() = context.bluetoothAdapter?.isEnabled ?: false

    private val bluetoothConnectionPermissionGranted
        get() = bleClient.isConnectRuntimePermissionGranted

    val precondition: PreconditionState
        get() = when {
            !bluetoothAvailable -> BLUETOOTH_NOT_AVAILABLE
            !bluetoothConnectionPermissionGranted -> BLUETOOTH_CONNECTION_PERMISSION_NOT_GRANTED
            !bluetoothEnabled -> BLUETOOTH_NOT_ENABLED
            else -> READY
        }

    val preconditionStream: Observable<PreconditionState> =
        Observable.merge(observeBluetoothState(), observeConnectionPermission())
            .map { precondition }
            .distinctUntilChanged()
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext { Log.i(TAG, "Current state: $it") }

    init {
        Log.i(TAG, "Initial state: $precondition")
    }

    private fun observeBluetoothState() = Observable.create<Boolean> { emitter ->
        val receiver = BluetoothAdapterReceiver { emitter.onNext(bluetoothEnabled) }
        emitter.setCancellable { context.unregisterReceiver(receiver) }
        context.registerReceiver(receiver, receiver.filter)
    }

    // Android does not provide a receiver or callback other than intents, so we have to poll.
    // The process gets killed when permissions are lost, so we only care about receiving permission.
    private fun observeConnectionPermission(): Observable<Boolean> =
        Observable.interval(0, 1L, TimeUnit.SECONDS)
            .map { bluetoothConnectionPermissionGranted }
            .filter { granted -> granted }
            .take(1) // cancels timer once permission is granted
}

private class BluetoothAdapterReceiver(private val onStateChanged: () -> Unit) : BroadcastReceiver() {
    val filter = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
    override fun onReceive(context: Context, intent: Intent) = onStateChanged()
}
