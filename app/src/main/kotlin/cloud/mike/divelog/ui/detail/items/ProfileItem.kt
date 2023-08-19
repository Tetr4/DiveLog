package cloud.mike.divelog.ui.detail.items

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.ZeroCornerSize
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cloud.mike.divelog.data.dives.DepthProfile
import cloud.mike.divelog.data.dives.Dive
import cloud.mike.divelog.localization.formatDepthMeters
import cloud.mike.divelog.localization.formatTemperatureCelsius
import cloud.mike.divelog.ui.DiveTheme
import cloud.mike.divelog.ui.common.chart.DepthChart

@Composable
fun ProfileItem(
    profile: DepthProfile,
    maxDepthMeters: Float?,
    minTemperatureCelsius: Float?,
    modifier: Modifier = Modifier,
) {
    Card(modifier = modifier.fillMaxWidth()) {
        // TODO points of interest?
        Box {
            DepthChart(
                modifier = Modifier.height(128.dp),
                profile = profile,
            )
            if (maxDepthMeters != null) {
                Label(
                    modifier = Modifier.align(Alignment.BottomEnd),
                    text = maxDepthMeters.formatDepthMeters(),
                    shape = createShape(topStart = MaterialTheme.shapes.small.topStart),
                )
            }
            if (minTemperatureCelsius != null) {
                Label(
                    modifier = Modifier.align(Alignment.BottomStart),
                    text = minTemperatureCelsius.formatTemperatureCelsius(),
                    shape = createShape(topEnd = MaterialTheme.shapes.small.topEnd),
                )
            }
        }
    }
}

@Composable
private fun Label(
    text: String,
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.small,
) {
    Surface(
        modifier = modifier,
        shape = shape,
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
    ) {
        Text(
            modifier = Modifier.padding(8.dp),
            text = text,
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun Preview() {
    DiveTheme {
        ProfileItem(
            profile = DepthProfile.sample,
            maxDepthMeters = Dive.sample.maxDepthMeters,
            minTemperatureCelsius = Dive.sample.minTemperatureCelsius,
        )
    }
}

private fun createShape(
    topStart: CornerSize = ZeroCornerSize,
    topEnd: CornerSize = ZeroCornerSize,
    bottomEnd: CornerSize = ZeroCornerSize,
    bottomStart: CornerSize = ZeroCornerSize,
) = RoundedCornerShape(
    topStart = topStart,
    topEnd = topEnd,
    bottomEnd = bottomEnd,
    bottomStart = bottomStart,
)
