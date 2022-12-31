package cloud.mike.divelog.bluetooth.connector

import android.util.Log
import cloud.mike.divelog.bluetooth.utils.toHexString
import com.polidea.rxandroidble2.NotificationSetupMode
import com.polidea.rxandroidble2.RxBleConnection
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.rx2.asFlow
import kotlinx.coroutines.rx2.await
import java.util.UUID

private val TAG = Connection::class.java.simpleName

class Connection(private val rxBleConnection: RxBleConnection) {
    suspend fun readCharacteristic(uuid: UUID): ByteArray {
        val response = rxBleConnection.readCharacteristic(uuid).await()
        Log.v(TAG, "<<< $uuid | ${response.toHexString()}")
        return response
    }

    suspend fun writeCharacteristic(uuid: UUID, bytes: ByteArray) {
        Log.v(TAG, ">>> $uuid | ${bytes.toHexString()}")
        rxBleConnection.writeCharacteristic(uuid, bytes).await()
    }

    fun setupNotification(uuid: UUID): Flow<ByteArray> =
        rxBleConnection.setupNotification(uuid, NotificationSetupMode.QUICK_SETUP)
            .flatMap { notificationObservable -> notificationObservable }
            .doOnNext { Log.v(TAG, "<<< $uuid | ${it.toHexString()}") }
            .asFlow()
}
