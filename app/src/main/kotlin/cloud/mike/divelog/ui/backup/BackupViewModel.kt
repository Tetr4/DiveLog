package cloud.mike.divelog.ui.backup

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cloud.mike.divelog.data.backup.BackupService
import cloud.mike.divelog.data.logging.logError
import cloud.mike.divelog.localization.errors.ErrorMessage
import cloud.mike.divelog.localization.errors.ErrorService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface BackupState {
    data object Idle : BackupState
    data object InProgress : BackupState
    data class Error(val message: ErrorMessage) : BackupState
    data object BackupCreated : BackupState
    data object BackupRestored : BackupState
}

class BackupViewModel(
    private val backupService: BackupService,
    private val errorService: ErrorService,
) : ViewModel() {

    val uiState = MutableStateFlow<BackupState>(BackupState.Idle)

    fun startBackup(uri: Uri) {
        viewModelScope.launch {
            try {
                uiState.update { BackupState.InProgress }
                backupService.createBackup(uri)
                uiState.update { BackupState.BackupCreated }
            } catch (e: Exception) {
                logError(e)
                uiState.update { BackupState.Error(errorService.createMessage(e)) }
            }
        }
    }

    fun restoreBackup(uri: Uri) {
        viewModelScope.launch {
            try {
                uiState.update { BackupState.InProgress }
                backupService.restoreBackup(uri)
                uiState.update { BackupState.BackupRestored }
            } catch (e: Exception) {
                logError(e)
                uiState.update { BackupState.Error(errorService.createMessage(e)) }
            }
        }
    }
}
