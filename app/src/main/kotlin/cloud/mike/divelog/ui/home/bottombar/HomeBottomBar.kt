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
    onShowImport: () -> Unit,
    onShowDiveSpots: () -> Unit,
    onShowAdd: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BottomAppBar(
        modifier = modifier,
        actions = {
            TooltipButton(
                icon = Icons.Filled.Bluetooth,
                description = stringResource(R.string.home_button_bluetooth_import),
                onClick = onShowImport,
            )
            TooltipButton(
                description = stringResource(R.string.home_button_dive_spots),
                icon = Icons.Filled.Map,
                onClick = onShowDiveSpots,
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onShowAdd) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = stringResource(R.string.home_button_add_dive),
                )
            }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TooltipButton(
    icon: ImageVector,
    description: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    PlainTooltipBox(
        modifier = modifier,
        tooltip = { Text(description) },
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier.tooltipAnchor(),
        ) {
            Icon(icon, contentDescription = description)
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun Preview() {
    DiveTheme {
        HomeBottomBar(
            onShowImport = {},
            onShowDiveSpots = {},
            onShowAdd = {},
        )
    }
}
