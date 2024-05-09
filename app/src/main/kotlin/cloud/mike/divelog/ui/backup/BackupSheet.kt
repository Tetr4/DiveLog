package cloud.mike.divelog.ui.backup

import android.content.res.Configuration
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cloud.mike.divelog.R
import cloud.mike.divelog.localization.errors.ErrorMessage
import cloud.mike.divelog.ui.DiveTheme
import cloud.mike.divelog.ui.backup.states.BackupErrorView
import cloud.mike.divelog.ui.backup.states.BackupIdleView
import cloud.mike.divelog.ui.backup.states.BackupProgressView
import cloud.mike.divelog.ui.backup.states.BackupSuccessView
import com.sebaslogen.resaca.viewModelScoped
import org.koin.java.KoinJavaComponent.getKoin

@Composable
fun BackupSheet(
    modifier: Modifier = Modifier,
) {
    // Viewmodel is scoped to sheet, so a new one is created when closing and opening sheet.
    val viewModel: BackupViewModel = viewModelScoped { getKoin().get() }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    BackupSheet(
        modifier = modifier,
        onStartBackup = viewModel::startBackup,
        onRestoreBackup = viewModel::restoreBackup,
        uiState = uiState,
    )
}

@Composable
private fun BackupSheet(
    uiState: BackupState,
    onStartBackup: (Uri) -> Unit,
    onRestoreBackup: (Uri) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    LaunchedEffect(uiState) {
        if (uiState is BackupState.BackupRestored && uiState.restartRequired) {
            context.restartApp()
        }
    }
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        when (uiState) {
            is BackupState.Idle -> BackupIdleView(
                onStartBackup = onStartBackup,
                onRestoreBackup = onRestoreBackup,
            )
            is BackupState.InProgress -> BackupProgressView()
            is BackupState.Error -> BackupErrorView(
                message = uiState.message,
            )
            is BackupState.BackupCreated -> BackupSuccessView(
                message = stringResource(R.string.backup_status_backup_created),
            )
            is BackupState.BackupRestored -> BackupSuccessView(
                message = stringResource(R.string.backup_status_backup_restored),
            )
        }
    }
}

private class StateProvider : PreviewParameterProvider<BackupState> {
    override val values = sequenceOf(
        BackupState.Idle,
        BackupState.InProgress,
        BackupState.Error(ErrorMessage("Error message")),
        BackupState.BackupCreated,
        BackupState.BackupRestored(restartRequired = false),
    )
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun PreviewIdle(
    @PreviewParameter(StateProvider::class) state: BackupState,
) {
    DiveTheme {
        Surface {
            BackupSheet(
                uiState = state,
                onStartBackup = {},
                onRestoreBackup = {},
            )
        }
    }
}
