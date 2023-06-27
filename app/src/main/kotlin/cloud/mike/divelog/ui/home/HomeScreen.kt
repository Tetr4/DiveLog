package cloud.mike.divelog.ui.home

import android.content.res.Configuration
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.BottomAppBar
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FabPosition
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cloud.mike.divelog.data.dives.Dive
import cloud.mike.divelog.ui.home.list.DiveList
import cloud.mike.divelog.ui.home.sheet.ImportSheet
import com.google.accompanist.themeadapter.material.MdcTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(
    uiState: HomeState,
    onShowDetail: (Dive) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true,
    )
    val fabShape = MaterialTheme.shapes.small.copy(CornerSize(percent = 50))

    fun showAddDive() = Unit // TODO
    fun showCloudSettings() = Unit // TODO
    fun showBluetoothImport() {
        scope.launch { sheetState.show() }
    }

    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetShape = MaterialTheme.shapes.large.copy(
            bottomStart = CornerSize(0.dp),
            bottomEnd = CornerSize(0.dp),
        ),
        sheetContent = {
            ImportSheet()
        },
    ) {
        Scaffold(
            modifier = Modifier.systemBarsPadding(),
            floatingActionButtonPosition = FabPosition.End,
            isFloatingActionButtonDocked = true,
            floatingActionButton = {
                FloatingActionButton(shape = fabShape, onClick = ::showAddDive) {
                    Icon(Icons.Filled.Add, contentDescription = "Add Dive")
                }
            },
            bottomBar = {
                BottomAppBar(cutoutShape = fabShape) {
                    IconButton(onClick = ::showCloudSettings) {
                        Icon(Icons.Default.CloudDone, "")
                    }
                    IconButton(onClick = ::showBluetoothImport) {
                        Icon(Icons.Filled.Bluetooth, "")
                    }
                }
            },
        ) { innerPadding ->
            DiveList(
                Modifier.padding(innerPadding),
                dives = uiState.dives.orEmpty(),
                onDiveClicked = onShowDetail,
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun Preview() {
    MdcTheme {
        HomeScreen(
            uiState = HomeState(),
            onShowDetail = {},
        )
    }
}
