package cloud.mike.divelog.ui.home.bottombar

import android.content.res.Configuration
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.PlainTooltipBox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import cloud.mike.divelog.R
import cloud.mike.divelog.ui.DiveTheme

@Composable
fun HomeBottomBar(
    showBluetoothImport: () -> Unit,
    showDiveSpots: () -> Unit,
    showAddDive: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BottomAppBar(
        modifier = modifier,
        actions = {
            ButtonWithTooltip(
                text = stringResource(R.string.home_button_bluetooth_import),
                icon = Icons.Filled.Bluetooth,
                onClick = showBluetoothImport,
            )
            ButtonWithTooltip(
                text = stringResource(R.string.home_button_dive_spots),
                onClick = showDiveSpots,
                icon = Icons.Filled.Map,
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = showAddDive) {
                Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.home_button_add_dive))
            }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ButtonWithTooltip(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
) {
    PlainTooltipBox(
        tooltip = { Text(text) },
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier.tooltipAnchor(),
        ) {
            Icon(icon, contentDescription = text)
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun Preview() {
    DiveTheme {
        HomeBottomBar(
            showBluetoothImport = {},
            showDiveSpots = {},
            showAddDive = {},
        )
    }
}
