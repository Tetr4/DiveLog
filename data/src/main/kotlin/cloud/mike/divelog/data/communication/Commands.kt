@file:Suppress("TooManyFunctions")

package cloud.mike.divelog.data.communication

import cloud.mike.divelog.bluetooth.connector.Connection

// Commands
suspend fun Connection.startCommunication() = command(0xBB)
suspend fun Connection.hardwareFeatures() = command(0x60)
suspend fun Connection.sendHeaders() = command(0x61)
suspend fun Connection.sendCompactHeaders() = DiveHeader.fromPayload(command(0x6D))
suspend fun Connection.setClockAndDate() = command(0x62)
suspend fun Connection.customText() = command(0x63)
suspend fun Connection.getDiveProfile() = command(0x66)
suspend fun Connection.versionIdentity() = command(0x69)
suspend fun Connection.displayText() = command(0x6E)
suspend fun Connection.getHardwareDescriptor() = command(0x6A)
suspend fun Connection.resetAllSettings() = command(0x78)
suspend fun Connection.writeSettings() = command(0x77)
suspend fun Connection.readSettings() = command(0x72)
suspend fun Connection.exitCommunication() = command(0xFF) // this kills connection
