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
import com.polidea.rxandroidble2.RxBleClient
import io.reactivex.Observable

private val TAG = PairingService::class.java.simpleName

class PairingService(
    context: Context,
    private val preconditionService: PreconditionService,
    private val bleClient: RxBleClient,
) {
    val pairedDevices
        get() = if (preconditionService.precondition == PreconditionState.READY) {
            try {
                bleClient.bondedDevices.map { it.bluetoothDevice }
            } catch (_: UnsupportedOperationException) {
                emptyList() // device does not have bluetooth support
            }
        } else {
            emptyList()
        }

    private val bondingStream = Observable.create<BluetoothDevice> { emitter ->
        val receiver = BondingReceiver(emitter::onNext)
        emitter.setCancellable { context.unregisterReceiver(receiver) }
        context.registerReceiver(receiver, receiver.filter)
    }

    val pairedDevicesStream = Observable.combineLatest(
        bondingStream,
        preconditionService.preconditionStream, // we may have access to bonded devices after receiving permission
    ) { _, _ -> pairedDevices }
}

private class BondingReceiver(private val onBondStateChanged: (BluetoothDevice) -> Unit) : BroadcastReceiver() {
    val filter = IntentFilter().apply {
        addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED)
    }

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
