package cloud.mike.divelog.data.communication

import cloud.mike.divelog.bluetooth.connector.Connection
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.reduce
import kotlinx.coroutines.flow.transformWhile
import java.util.UUID

private val UART_DATA_RX = UUID.fromString("00000001-0000-1000-8000-008025000000") // WRITE - NO RESPONSE
private val UART_DATA_TX = UUID.fromString("00000002-0000-1000-8000-008025000000") // NOTIFY
private val UART_CREDITS_RX = UUID.fromString("00000003-0000-1000-8000-008025000000") // WRITE

/** This is an implementation of the OSTC transfer protocol. */
internal suspend fun Connection.sendCommand(command: Int, payload: ByteArray? = null): ByteArray {
    var credits = 0
    suspend fun ensureCredits() {
        if (credits < 30) {
            sendCredits(100)
            credits += 100
        }
    }

    val response = subscribeData {
        ensureCredits()
        sendByte(command)
    }
        .onEach {
            if (payload != null && it.size == 1 && it.first() == command.toByte()) {
                sendData(payload) // TODO only send this after the first echo
            } else {
                credits--
                ensureCredits()
            }
        }
        .takeUntilEndOfTransmission()
        .reduce { acc, value -> acc + value } // combine all payloads
    return response.drop(1).dropLast(1).toByteArray() // drop command and stop byte
}

internal suspend fun Connection.sendByte(data: Int) = sendData(byteArrayOf(data.toByte()))

private suspend fun Connection.sendData(bytes: ByteArray) = write(UART_DATA_RX, bytes)
private suspend fun Connection.sendCredits(credits: Int = 254) = write(UART_CREDITS_RX, byteArrayOf(credits.toByte()))
private fun Connection.subscribeData(onSubscribe: suspend () -> Unit) = setupNotification(UART_DATA_TX, onSubscribe)

private fun Flow<ByteArray>.takeUntilEndOfTransmission() = transformWhile {
    emit(it)
    it.last() != 0X4D.toByte() // TODO this breaks when stop byte is part of message
}
