package cloud.mike.divelog.bluetooth.connector

import android.util.Log
import cloud.mike.divelog.bluetooth.utils.toHexString
import com.polidea.rxandroidble2.NotificationSetupMode
import com.polidea.rxandroidble2.RxBleConnection
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.rx2.asFlow
import kotlinx.coroutines.rx2.await
import java.util.UUID

private val TAG = Connection::class.java.simpleName

class Connection(private val rxBleConnection: RxBleConnection) {
    suspend fun read(uuid: UUID): ByteArray {
        val response = rxBleConnection.readCharacteristic(uuid).await()
        Log.v(TAG, "<<< $uuid | ${response.toHexString()}")
        return response
    }

    suspend fun write(uuid: UUID, bytes: ByteArray) {
        Log.v(TAG, ">>> $uuid | ${bytes.toHexString()}")
        rxBleConnection.writeCharacteristic(uuid, bytes).await()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun setupNotification(uuid: UUID, onSetupCompleted: suspend () -> Unit = {}): Flow<ByteArray> =
        rxBleConnection.setupNotification(uuid, NotificationSetupMode.DEFAULT)
            .asFlow()
            .onEach { onSetupCompleted() }
            .flatMapConcat { it.asFlow() }
            .onEach { Log.v(TAG, "<<< $uuid | ${it.toHexString()}") }
}
