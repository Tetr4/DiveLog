package cloud.mike.divelog.data.importer.shearwater

import cloud.mike.divelog.bluetooth.connector.Connection
import cloud.mike.divelog.data.dives.Dive
import cloud.mike.divelog.data.importer.ImportConnection
import cloud.mike.divelog.data.importer.shearwater.frames.DiveData
import cloud.mike.divelog.data.importer.shearwater.frames.ManifestRecord
import cloud.mike.divelog.data.importer.shearwater.frames.parseDiveData
import cloud.mike.divelog.data.importer.shearwater.frames.parseManifestRecords
import java.time.LocalDateTime

private const val MANIFEST_ADDRESS_START = 0xE0000000
private const val MANIFEST_SIZE_BYTES = 1536
private const val DIVE_ADDRESS_BASE = 0x80000000
private const val DIVE_SIZE_BYTES = 16777215
private const val RECORDS_PER_MANIFEST = MANIFEST_SIZE_BYTES / ManifestRecord.SIZE_BYTES

/**
 * Shearwater API is not public, so this is based on code in
 * [libdivecomputer](https://github.com/libdivecomputer/libdivecomputer/blob/master/src/shearwater_petrel.c).
 *
 * This only supports the "Petrel Native Format".
 */
internal class ShearwaterConnection(
    private val connection: Connection,
) : ImportConnection {

    // TODO This was not tested yet.
    override suspend fun fetchDives(
        initialDiveNumber: Int,
        isAlreadyImported: suspend (LocalDateTime) -> Boolean,
        onProgress: suspend (Float) -> Unit,
    ): List<Dive> {
        val recordsToImport = connection.fetchManifestRecords()
            .filterIsInstance<ManifestRecord.Dive>()
            .filter { !isAlreadyImported(it.timestamp) }
        return recordsToImport
            .map { record -> connection.fetchDiveData(record) }
            .mapIndexed { index, data -> data.toDive(initialDiveNumber + index) }
    }
}

private suspend fun Connection.fetchManifestRecords(): List<ManifestRecord> = buildList {
    do {
        // TODO Check if this actually gives a different result every time. Seems fishy.
        val page = fetchManifestPage()
        addAll(page)
    } while (page.size == RECORDS_PER_MANIFEST) // last page is only partially filled or empty
}

private suspend fun Connection.fetchManifestPage(): List<ManifestRecord> =
    downloadMemory(MANIFEST_ADDRESS_START, MANIFEST_SIZE_BYTES).parseManifestRecords()

private suspend fun Connection.fetchDiveData(record: ManifestRecord.Dive): DiveData =
    downloadMemory(DIVE_ADDRESS_BASE + record.diveAddress, DIVE_SIZE_BYTES).parseDiveData()
