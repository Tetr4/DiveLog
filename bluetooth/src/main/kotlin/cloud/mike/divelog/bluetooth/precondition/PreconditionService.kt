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
import com.polidea.rxandroidble3.RxBleClient
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.isActive

private val TAG = PreconditionService::class.java.simpleName

/**
 * Service for resolving bluetooth LE connecting preconditions.
 * @see [PreconditionState]
 */
class PreconditionService(
    private val context: Context,
    private val bleClient: RxBleClient,
) {
    val precondition: PreconditionState
        get() = when {
            !bluetoothAvailable -> BLUETOOTH_NOT_AVAILABLE
            !bluetoothConnectionPermissionGranted -> BLUETOOTH_CONNECTION_PERMISSION_NOT_GRANTED
            !bluetoothEnabled -> BLUETOOTH_NOT_ENABLED
            else -> READY
        }

    val preconditionFlow = merge(
        bluetoothStateFlow,
        connectionPermissionFlow,
    )
        .map { precondition }
        .distinctUntilChanged()
        .onEach { Log.i(TAG, "Current state: $it") }

    private val bluetoothStateFlow
        get() = callbackFlow {
            send(bluetoothEnabled)
            val receiver = BluetoothAdapterReceiver { trySendBlocking(bluetoothEnabled) }
            context.registerReceiver(receiver, receiver.filter)
            awaitClose { context.unregisterReceiver(receiver) }
        }

    private val connectionPermissionFlow
        get() = channelFlow {
            // We have to poll, because Android does not provide a receiver or callback other than intents.
            while (isActive) {
                send(bluetoothConnectionPermissionGranted)
                delay(1000)
            }
        }
            // The process gets killed when permissions are lost, so no need for polling after receiving permissions.
            .filter { granted -> granted }
            .take(1)

    private val bluetoothAvailable
        get() = context.bluetoothAdapter != null

    private val bluetoothEnabled
        get() = context.bluetoothAdapter?.isEnabled ?: false

    private val bluetoothConnectionPermissionGranted
        get() = bleClient.isConnectRuntimePermissionGranted

    init {
        Log.i(TAG, "Initial state: $precondition")
    }
}

private class BluetoothAdapterReceiver(private val onStateChanged: () -> Unit) : BroadcastReceiver() {
    val filter = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
    override fun onReceive(context: Context, intent: Intent) = onStateChanged()
}
