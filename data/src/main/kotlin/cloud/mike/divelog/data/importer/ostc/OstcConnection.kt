package cloud.mike.divelog.data.importer.ostc

import cloud.mike.divelog.bluetooth.connector.Connection
import cloud.mike.divelog.data.dives.Dive
import cloud.mike.divelog.data.dives.toDive
import cloud.mike.divelog.data.importer.ImportConnection
import cloud.mike.divelog.data.importer.ostc.frames.DiveHeaderCompactFrame
import cloud.mike.divelog.data.importer.ostc.frames.DiveHeaderFullFrame
import cloud.mike.divelog.data.importer.ostc.frames.DiveProfileFrame
import cloud.mike.divelog.data.importer.ostc.frames.ProfileHeaderFrame
import cloud.mike.divelog.data.importer.ostc.frames.ProfileSampleFrame
import cloud.mike.divelog.data.importer.ostc.frames.parseCompactHeaders
import cloud.mike.divelog.data.importer.ostc.frames.parseProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.runningReduce
import kotlinx.coroutines.flow.withIndex
import java.time.LocalDateTime
import java.util.UUID

private val UART_DATA_RX = UUID.fromString("00000001-0000-1000-8000-008025000000") // WRITE - NO RESPONSE
private val UART_DATA_TX = UUID.fromString("00000002-0000-1000-8000-008025000000") // NOTIFY
private val UART_CREDITS_RX = UUID.fromString("00000003-0000-1000-8000-008025000000") // WRITE

private const val MAX_CREDITS = 254
private const val MIN_CREDITS = 32

/**
 * The "length of profile data" we get from the firmware includes the [ProfileHeaderFrame], all [ProfileSampleFrame]s
 * and two stop bytes. I have no idea where it gets these 3 additional bytes from. Off by one error?
 */
private const val UNACCOUNTED_PROFILE_SIZE_BYTES = 3

internal class OstcConnection(
    private val connection: Connection,
    private val onDisconnect: () -> Unit,
) : ImportConnection {

    private var uartCredits = 0

    /**
     * Workflow:
     * 1. [startCommunication]
     * 2. [fetchCompactHeaders]
     * 3. [fetchDiveProfile] for each header
     * 4. [exitCommunication]
     */
    override suspend fun fetchDives(
        currentDiveNumber: Int,
        isAlreadyImported: suspend (LocalDateTime) -> Boolean,
        onProgress: suspend (Float) -> Unit,
    ): List<Dive> {
        startCommunication()
        val headers = fetchCompactHeaders()
            .filter { !isAlreadyImported(it.timestamp) }
        val profiles = headers.mapIndexed { index, header ->
            onProgress(index.toFloat() / headers.size)
            fetchDiveProfile(header)
        }
        val dives = profiles.mapIndexed { index, profile ->
            profile.toDive(currentDiveNumber + index + 1)
        }
        exitCommunication()
        return dives
    }

    private suspend fun startCommunication() = sendCommand(
        command = 0xBB,
        responseLengthBytes = 0,
        checkStopByte = true, // Will not echo if already in download mode, so we check the stop byte
    )

    private suspend fun exitCommunication() {
        connection.sendByte(0xFF) // This kills connection, so no command echo
        onDisconnect()
    }

    private suspend fun fetchCompactHeaders(): List<DiveHeaderCompactFrame> = sendCommand(
        command = 0x6D,
        responseLengthBytes = 4096,
    ).parseCompactHeaders()

    private suspend fun fetchDiveProfile(header: DiveHeaderCompactFrame): DiveProfileFrame = sendCommand(
        command = 0x66,
        responseLengthBytes = DiveHeaderFullFrame.SIZE_BYTES + header.profileSize - UNACCOUNTED_PROFILE_SIZE_BYTES,
        commandParameters = byteArrayOf(header.profileNumber.toByte()),
    ).parseProfile()

    private suspend fun ensureCredits() {
        if (uartCredits <= MIN_CREDITS) {
            connection.sendCredits(MAX_CREDITS - uartCredits)
            uartCredits += (MAX_CREDITS - uartCredits)
        }
    }

    /**
     * This is an implementation of the Terminal IO and hwOS transfer protocol.
     *
     * Workflow:
     * 1. Send credits to [UART_CREDITS_RX]
     * 2. Set up [UART_DATA_TX] notification
     * 3. Send [command] to [UART_DATA_RX]
     * 4. Receive command echo via [UART_DATA_TX]
     * 5. (Optional: Send [commandParameters] via [UART_DATA_RX]
     * 6. Receive response payload (multiple BLE messages) and send UART credits if necessary
     * 7. Receive stop byte (0x4D)
     *
     * Note: We don't track UART client credits, because we send just a few bytes. Not sure why this system is even
     * needed in the first place, as it should be handled by the bluetooth stack.
     */
    private suspend fun sendCommand(
        command: Int,
        responseLengthBytes: Int,
        checkStopByte: Boolean = false,
        commandParameters: ByteArray? = null,
    ): ByteArray {
        ensureCredits()
        val combinedResponse = connection.subscribeData(
            onSubscribed = { connection.sendByte(command) },
        )
            .onEach { uartCredits-- }
            .onEach { ensureCredits() }
            .onFirst { commandParameters?.let { connection.sendData(it) } } // Send payload after command echo response
            .runningReduce { accumulator, value -> accumulator + value } // Combine message payloads
            // Stop when combined response (minus echo and stop byte) has expected length
            .first { (it.size - 2 >= responseLengthBytes) || (checkStopByte && it.isStopByte) }
        return combinedResponse.drop(1).dropLast(1).toByteArray() // Drop echo and stop byte
    }
}

private suspend fun Connection.sendByte(data: Int) = sendData(byteArrayOf(data.toByte()))
private suspend fun Connection.sendData(bytes: ByteArray) = write(UART_DATA_RX, bytes)
private suspend fun Connection.sendCredits(credits: Int) = write(UART_CREDITS_RX, byteArrayOf(credits.toByte()))
private fun Connection.subscribeData(onSubscribed: suspend () -> Unit) = setupNotification(UART_DATA_TX, onSubscribed)

private val ByteArray.isStopByte: Boolean
    get() = size == 1 && first() == 0x4D.toByte()

private fun <T> Flow<T>.onFirst(function: suspend (value: T) -> Unit) = withIndex()
    .onEach { (index, value) -> if (index == 0) function(value) }
    .map { it.value }
