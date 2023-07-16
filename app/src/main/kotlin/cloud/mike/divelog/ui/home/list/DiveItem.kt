package cloud.mike.divelog.ui.home.list

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ScubaDiving
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cloud.mike.divelog.data.dives.Dive
import cloud.mike.divelog.localization.format
import cloud.mike.divelog.localization.formatDepthMeters
import cloud.mike.divelog.ui.common.DepthChart
import com.google.accompanist.themeadapter.material3.Mdc3Theme

@Composable
fun DiveItem(
    modifier: Modifier = Modifier,
    dive: Dive,
    onClick: () -> Unit,
) {
    ListItem(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        leadingContent = {
            Box(modifier = Modifier.size(64.dp)) {
                when (val profile = dive.diveProfile) {
                    null -> DiverIcon(modifier = Modifier.align(Alignment.Center))
                    else -> DepthChart(profile = profile)
                }
            }
        },
        headlineContent = { Text(dive.location) },
        overlineContent = { Text(dive.start.format()) },
        supportingContent = { Text(dive.formatInfoLine()) },
        trailingContent = { DiveNumber(dive.number) },
    )
}

@Composable
private fun DiverIcon(
    modifier: Modifier = Modifier,
) {
    Icon(
        modifier = modifier.padding(16.dp),
        imageVector = Icons.Default.ScubaDiving,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.onBackground,
    )
}

@Composable
private fun DiveNumber(number: Int) {
    Box(
        modifier = Modifier.heightIn(min = 64.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "#${number.format()}",
            style = MaterialTheme.typography.labelLarge,
        )
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun PreviewNoProfile() {
    Mdc3Theme {
        DiveItem(
            dive = Dive.sample.copy(diveProfile = null),
            onClick = {},
        )
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun Preview() {
    Mdc3Theme {
        DiveItem(
            dive = Dive.sample,
            onClick = {},
        )
    }
}

@Composable
@ReadOnlyComposable
private fun Dive.formatInfoLine(): String = listOfNotNull(
    diveTime.format(),
    maxDepthMeters?.formatDepthMeters(),
).joinToString(" | ")
