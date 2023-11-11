package cloud.mike.divelog.ui.home.list.item

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
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
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cloud.mike.divelog.data.dives.Dive
import cloud.mike.divelog.localization.format
import cloud.mike.divelog.localization.formatDepthMeters
import cloud.mike.divelog.localization.formatDiveNumber
import cloud.mike.divelog.ui.DiveTheme
import cloud.mike.divelog.ui.common.chart.DepthChart
import java.time.format.FormatStyle

@Composable
fun DiveListItem(
    dive: Dive,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ListItem(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        leadingContent = {
            LeadingImage {
                when (val profile = dive.profile) {
                    // TODO We could also show map marker or pictures here
                    null -> DiverIcon(modifier = Modifier.align(Alignment.Center))
                    else -> DepthChart(profile = profile)
                }
            }
        },
        // TODO clean up
        headlineContent = { Text(dive.location?.name ?: dive.startTime?.format(FormatStyle.SHORT) ?: "") },
        supportingContent = { Text(dive.formatInfoLine()) },
        trailingContent = { DiveNumber(dive.number) },
    )
}

@Composable
private fun LeadingImage(content: @Composable BoxScope.() -> Unit) {
    Box(
        modifier = Modifier
            .size(56.dp)
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.surfaceColorAtElevation(6.dp)),
        content = content,
    )
}

@Composable
private fun DiverIcon(modifier: Modifier = Modifier) {
    Icon(
        modifier = modifier.padding(8.dp),
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
            text = number.formatDiveNumber(),
            style = MaterialTheme.typography.labelLarge,
        )
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun PreviewNoProfile() {
    DiveTheme {
        DiveListItem(
            dive = Dive.sample.copy(profile = null),
            onClick = {},
        )
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun Preview() {
    DiveTheme {
        DiveListItem(
            dive = Dive.sample,
            onClick = {},
        )
    }
}

@Composable
@ReadOnlyComposable
private fun Dive.formatInfoLine(): String = listOfNotNull(
    duration.format(),
    maxDepthMeters?.formatDepthMeters(),
).joinToString(" | ")
