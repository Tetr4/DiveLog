package cloud.mike.divelog.data.communication

import cloud.mike.divelog.bluetooth.connector.Connection
import cloud.mike.divelog.data.communication.frames.parseCompactHeaders
import cloud.mike.divelog.data.communication.frames.parseProfile

suspend fun Connection.startCommunication() = sendCommand(0xBB)
suspend fun Connection.exitCommunication() = sendByte(0xFF) // this kills connection

suspend fun Connection.sendCompactHeaders() = sendCommand(0x6D)
    .parseCompactHeaders()

suspend fun Connection.getDiveProfile(number: Int) = sendCommand(0x66, byteArrayOf(number.toByte()))
    .parseProfile()
