package cloud.mike.divelog.ui.common.chart

import android.content.res.Configuration
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cloud.mike.divelog.data.dives.DiveProfile
import cloud.mike.divelog.ui.DiveTheme
import cloud.mike.divelog.ui.common.sample

private const val MAX_DATA_POINTS = 200 // Reduce huge datasets
private const val DEPTH_PADDING_PERCENT = 1.1f // Ensure chart never touches bottom

@Composable
fun DepthChart(
    profile: DiveProfile,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
) {
    val dataPoints by remember(profile.depthCentimeters) {
        derivedStateOf {
            profile.depthCentimeters.toList().sample(MAX_DATA_POINTS)
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val maxValue = dataPoints.max().toFloat() * DEPTH_PADDING_PERCENT
        val maxPoints = dataPoints.size.toFloat()
        val coordinates = dataPoints.mapIndexed { index, value ->
            Offset(
                x = (index / (maxPoints - 1f)) * size.width,
                y = (value / maxValue) * size.height,
            )
        }
        val path = Path().apply {
            if (coordinates.isNotEmpty()) {
                moveTo(coordinates.first().x, 0f)
                coordinates.forEach { lineTo(it.x, it.y) }
                lineTo(coordinates.last().x, 0f)
                close()
            }
        }
        drawPath(
            path = path,
            brush = Brush.verticalGradient(
                listOf(color, color.copy(alpha = 0.1f)),
            ),
        )
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun Preview() {
    DiveTheme {
        DepthChart(
            modifier = Modifier.height(64.dp),
            profile = DiveProfile.sample,
        )
    }
}
