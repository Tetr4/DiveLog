package cloud.mike.divelog.ui.create

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cloud.mike.divelog.R
import cloud.mike.divelog.data.dives.Dive
import cloud.mike.divelog.localization.errors.ErrorMessage
import cloud.mike.divelog.ui.DiveTheme
import cloud.mike.divelog.ui.create.item.DiveStartItem
import cloud.mike.divelog.ui.create.item.DiveTimeItem
import com.sebaslogen.resaca.viewModelScoped
import org.koin.java.KoinJavaComponent.getKoin

@Composable
fun CreateDiveSheet(
    onDiveCreated: (Dive) -> Unit,
    onShowError: suspend (ErrorMessage) -> Unit,
    modifier: Modifier = Modifier,
) {
    // Viewmodel is scoped to sheet, so a new one is created when closing and opening sheet.
    val viewModel: CreateDiveViewModel = viewModelScoped { getKoin().get() }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    CreateDiveSheet(
        modifier = modifier,
        uiState = uiState,
        onSave = viewModel::save,
        onDiveCreated = onDiveCreated,
        onShowError = onShowError,
    )
}

@Composable
private fun CreateDiveSheet(
    uiState: CreateDiveState,
    onSave: (FormData) -> Unit,
    onDiveCreated: (Dive) -> Unit,
    onShowError: suspend (ErrorMessage) -> Unit,
    modifier: Modifier = Modifier,
) {
    val formState = rememberFormState()

    fun trySave() {
        val data = FormData(
            start = formState.start ?: return,
            diveTime = formState.diveTime ?: return,
        )
        onSave(data)
    }

    LaunchedEffect(uiState.saveState) {
        when (uiState.saveState) {
            is SaveState.Error -> onShowError(uiState.saveState.message)
            is SaveState.Success -> onDiveCreated(uiState.saveState.createdDive)
            is SaveState.Idle, is SaveState.Saving -> Unit
        }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = stringResource(R.string.create_dive_title),
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(24.dp))
        Divider()
        DiveStartItem(formState)
        Divider()
        DiveTimeItem(formState)
        Divider()
        Spacer(Modifier.height(24.dp))
        Button(
            enabled = formState.isValid,
            onClick = ::trySave,
        ) {
            Text(stringResource(R.string.create_dive_button_save))
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun Preview() {
    DiveTheme {
        Surface {
            CreateDiveSheet(
                modifier = Modifier.heightIn(min = 300.dp),
                uiState = CreateDiveState(),
                onSave = {},
                onDiveCreated = {},
                onShowError = {},
            )
        }
    }
}
