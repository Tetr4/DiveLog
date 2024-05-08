package cloud.mike.divelog.bluetooth.pairing

import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import cloud.mike.divelog.bluetooth.precondition.PreconditionService
import cloud.mike.divelog.bluetooth.precondition.PreconditionState
import cloud.mike.divelog.bluetooth.utils.getParcelableExtraCompat
import com.polidea.rxandroidble3.RxBleClient
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge

private val TAG = PairingService::class.java.simpleName

class PairingService(
    private val context: Context,
    private val preconditionService: PreconditionService,
    private val bleClient: RxBleClient,
) {
    val pairedDevices
        get() = if (preconditionService.precondition == PreconditionState.READY) {
            bleClient.bondedDevices.map { it.bluetoothDevice }
        } else {
            emptyList()
        }

    val pairedDevicesFlow = merge(
        deviceBondingFlow,
        preconditionService.preconditionFlow, // we may have access to bonded devices after receiving permission
    ).map { pairedDevices }

    private val deviceBondingFlow
        get() = callbackFlow {
            val receiver = BondingReceiver { trySendBlocking(it) }
            context.registerReceiver(receiver, receiver.filter)
            awaitClose { context.unregisterReceiver(receiver) }
        }
}

private class BondingReceiver(private val onBondStateChanged: (BluetoothDevice) -> Unit) : BroadcastReceiver() {
    val filter = IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED)
    override fun onReceive(context: Context, intent: Intent) {
        val device = intent.bluetoothDevice
        if (device != null) {
            Log.i(TAG, "Bonding changed: $device (${device.bonding})")
            onBondStateChanged(device)
        }
    }
}

private val Intent.bluetoothDevice: BluetoothDevice?
    get() = getParcelableExtraCompat<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
