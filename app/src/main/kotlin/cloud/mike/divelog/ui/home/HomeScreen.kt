package cloud.mike.divelog.ui.home

import android.content.res.Configuration
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.BottomAppBar
import androidx.compose.material.FabPosition
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import cloud.mike.divelog.data.dives.Dive
import com.google.accompanist.themeadapter.material.MdcTheme
import org.koin.androidx.compose.getViewModel

@Composable
fun HomeScreen(viewModel: HomeViewModel = getViewModel()) {
    val dives = viewModel.dives
    HomeScreen(dives = dives)
}

@Composable
fun HomeScreen(dives: List<Dive>?) {
    val fabShape = MaterialTheme.shapes.small.copy(CornerSize(percent = 50))

    fun showAddDive() = Unit // TODO
    fun showDiveDetails(dive: Dive) = Unit // TODO
    fun showCloudSettings() = Unit // TODO
    fun showBluetoothImport() = Unit // TODO

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
        }
    ) { innerPadding ->
        DiveList(
            Modifier.padding(innerPadding),
            dives = dives.orEmpty(),
            onDiveClicked = ::showDiveDetails
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun Preview() {
    MdcTheme {
        HomeScreen(dives = Dive.samples)
    }
}
