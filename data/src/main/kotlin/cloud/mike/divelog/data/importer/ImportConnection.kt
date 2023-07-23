package cloud.mike.divelog.data.importer

import cloud.mike.divelog.data.dives.Dive
import java.time.LocalDateTime

interface ImportConnection {
    suspend fun fetchDives(
        currentDiveNumber: Int,
        isAlreadyImported: suspend (LocalDateTime) -> Boolean,
        onProgress: suspend (Float) -> Unit,
    ): List<Dive>
}
