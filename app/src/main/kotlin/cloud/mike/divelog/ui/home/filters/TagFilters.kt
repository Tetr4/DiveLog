package cloud.mike.divelog.ui.home.filters

import android.content.res.Configuration
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cloud.mike.divelog.ui.DiveTheme
import cloud.mike.divelog.ui.spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagFilters(modifier: Modifier = Modifier) {
    // TODO this should contain user defined tags
    Row(
        modifier = modifier
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = MaterialTheme.spacing.screenPadding),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        FilterChip(
            selected = false,
            onClick = {},
            leadingIcon = { Icon(Icons.Default.Bedtime, contentDescription = null) },
            label = { Text("Night dive") },
        )
        FilterChip(
            selected = true,
            onClick = {},
            label = { Text("Cyprus") },
            leadingIcon = { Icon(Icons.Default.Check, contentDescription = null) },
        )
        FilterChip(
            selected = false,
            onClick = {},
            leadingIcon = { Icon(Icons.Default.Place, contentDescription = null) },
            label = { Text("Egypt") },
        )
        (0..5).map { index ->
            FilterChip(
                selected = false,
                onClick = {},
                label = { Text("Filter: $index") },
                leadingIcon = { Icon(Icons.Default.Place, contentDescription = null) },
            )
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun Preview() {
    DiveTheme {
        TagFilters()
    }
}
