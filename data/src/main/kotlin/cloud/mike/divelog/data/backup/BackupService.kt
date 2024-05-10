package cloud.mike.divelog.data.backup

import android.content.Context
import android.content.Intent
import android.net.Uri
import cloud.mike.divelog.persistence.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.system.exitProcess

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

    suspend fun restoreBackup(uri: Uri) {
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
        context.restartApp()
    }
}

private fun Context.restartApp() {
    val intent = packageManager.getLaunchIntentForPackage(packageName) ?: error("App has no launcher intent")
    val mainIntent = Intent.makeRestartActivityTask(intent.component)
    startActivity(mainIntent)
    exitProcess(0)
}
