package cloud.mike.divelog.ui.backup.states

import android.content.res.Configuration
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import cloud.mike.divelog.R
import cloud.mike.divelog.ui.DiveTheme
import java.time.LocalDate

private const val DB_MIME_TYPE = "application/octet-stream"

@Composable
fun BackupIdleView(
    onStartBackup: (Uri) -> Unit,
    onRestoreBackup: (Uri) -> Unit,
    modifier: Modifier = Modifier,
) {
    val createDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument(DB_MIME_TYPE),
        onResult = { uri -> uri?.let(onStartBackup) },
    )

    val openDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri -> uri?.let(onRestoreBackup) },
    )

    fun showCreateDocument() {
        val timestamp = LocalDate.now().toString() // ISO-8601
        createDocumentLauncher.launch("divelog-$timestamp.db")
    }

    fun showOpenDocument() {
        openDocumentLauncher.launch(arrayOf(DB_MIME_TYPE))
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ListItem(
            modifier = Modifier.clickable(onClick = ::showCreateDocument),
            leadingContent = { Icon(Icons.Default.Backup, contentDescription = null) },
            headlineContent = { Text(stringResource(R.string.backup_button_create_backup)) },
        )
        ListItem(
            modifier = Modifier.clickable(onClick = ::showOpenDocument),
            leadingContent = { Icon(Icons.Default.Restore, contentDescription = null) },
            headlineContent = { Text(stringResource(R.string.backup_button_restore_backup)) },
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun Preview() {
    DiveTheme {
        BackupIdleView(
            onStartBackup = {},
            onRestoreBackup = {},
        )
    }
}
