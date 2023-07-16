package cloud.mike.divelog.ui.home

import android.content.res.Configuration
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cloud.mike.divelog.data.dives.Dive
import cloud.mike.divelog.localization.errors.ErrorMessage
import cloud.mike.divelog.ui.home.list.DiveList
import cloud.mike.divelog.ui.home.sheet.ImportSheet
import com.google.accompanist.themeadapter.material3.Mdc3Theme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    uiState: HomeState,
    onShowDetail: (Dive) -> Unit,
    onSearch: (query: String) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val bluetoothSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    fun showAddDive() = Unit // TODO
    fun showCloudSettings() = Unit // TODO

    fun showBluetoothImport() {
        scope.launch { bluetoothSheetState.show() }
    }

    suspend fun showError(message: ErrorMessage) {
        snackbarHostState.showSnackbar(message.content)
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = { Text("My Dives") },
                scrollBehavior = scrollBehavior,
            )
        },
        bottomBar = {
            BottomBar(
                showCloudSettings = ::showCloudSettings,
                showBluetoothImport = ::showBluetoothImport,
                showAddDive = ::showAddDive,
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        DiveList(
            modifier = Modifier
                .padding(innerPadding)
                .padding(top = 16.dp),
            dives = uiState.dives.orEmpty(),
            onDiveClicked = onShowDetail,
            query = uiState.query,
            onQueryChanged = onSearch,
        )
    }

    if (!bluetoothSheetState.isCollapsed) {
        ModalBottomSheet(
            sheetState = bluetoothSheetState,
            onDismissRequest = { },
        ) {
            ImportSheet(
                modifier = Modifier.fillMaxHeight(0.6f),
                onShowError = ::showError,
            )
        }
    }
}

@Composable
private fun BottomBar(
    showCloudSettings: () -> Unit,
    showBluetoothImport: () -> Unit,
    showAddDive: () -> Unit,
) {
    BottomAppBar(
        actions = {
            IconButton(onClick = showCloudSettings) {
                Icon(Icons.Default.CloudDone, "Cloud Sync")
            }
            IconButton(onClick = showBluetoothImport) {
                Icon(Icons.Filled.Bluetooth, "Bluetooth Import")
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = showAddDive) {
                Icon(Icons.Filled.Add, contentDescription = "Add Dive")
            }
        },
    )
}

@Preview(showBackground = true, showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun Preview() {
    Mdc3Theme {
        HomeScreen(
            uiState = HomeState(),
            onShowDetail = {},
            onSearch = {},
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
private val SheetState.isCollapsed
    get() = currentValue == SheetValue.Hidden && targetValue == SheetValue.Hidden
