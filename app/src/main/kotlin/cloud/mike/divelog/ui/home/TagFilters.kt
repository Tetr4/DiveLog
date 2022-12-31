package cloud.mike.divelog.ui.home

import android.content.res.Configuration
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FilterChip
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Place
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.themeadapter.material.MdcTheme

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TagFilters(
    modifier: Modifier = Modifier,
) {
    // TODO this should contain user defined tags
    Row(
        modifier = Modifier
            .horizontalScroll(rememberScrollState())
            .then(modifier),
        horizontalArrangement = Arrangement
            .spacedBy(8.dp)
    ) {
        FilterChip(
            selected = false,
            onClick = { },
            leadingIcon = { Icon(Icons.Default.Bedtime, contentDescription = null) }
        ) {
            Text("Night dive")
        }
        FilterChip(
            selected = true,
            onClick = { },
            leadingIcon = { Icon(Icons.Default.Check, contentDescription = null) }
        ) {
            Text("Cyprus")
        }
        FilterChip(
            selected = false,
            onClick = { },
            leadingIcon = { Icon(Icons.Default.Place, contentDescription = null) }
        ) {
            Text("Egypt")
        }
        (0..5).map { index ->
            FilterChip(
                selected = false,
                onClick = { },
                leadingIcon = { Icon(Icons.Default.Place, contentDescription = null) }
            ) {
                Text("Filter: $index")
            }
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun Preview() {
    MdcTheme {
        TagFilters()
    }
}
