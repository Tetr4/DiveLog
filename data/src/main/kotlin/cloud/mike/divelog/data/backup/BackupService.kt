package cloud.mike.divelog.data.backup

import android.content.Context
import android.net.Uri
import androidx.annotation.CheckResult
import cloud.mike.divelog.persistence.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BackupService(
    private val context: Context,
) {

    suspend fun createBackup(uri: Uri) {
        withContext(Dispatchers.IO) {
            val databaseFile = context.getDatabasePath(AppDatabase.FILE_NAME)
            context.contentResolver.openOutputStream(uri)?.use { externalFileStream ->
                databaseFile.inputStream().use { databaseStream ->
                    databaseStream.copyTo(externalFileStream)
                }
            }
        }
    }

    @CheckResult
    suspend fun restoreBackup(uri: Uri): BackupResult {
        withContext(Dispatchers.IO) {
            val databaseFile = context.getDatabasePath(AppDatabase.FILE_NAME)
            context.contentResolver.openInputStream(uri)?.use { externalFileStream ->
                databaseFile.outputStream().use { databaseStream ->
                    externalFileStream.copyTo(databaseStream)
                }
            }
        }
        // Running migrations would require rebuilding the database. Also open data streams need to be reopened.
        // To resolve this we could add a complex abstraction layer, or we can just restart the app.
        return BackupResult.RESTART_REQUIRED
    }
}

enum class BackupResult { RESTART_REQUIRED }
