package cloud.mike.divelog.ui.home.list.item

import android.content.res.Configuration
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cloud.mike.divelog.data.dives.DiveProfile
import com.google.accompanist.themeadapter.material3.Mdc3Theme

@Composable
fun DepthChart(
    profile: DiveProfile,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
) {
    // TODO sample list to increase performance?
    val dataPoints = profile.depthCentimeters

    Canvas(
        modifier = modifier.fillMaxSize(),
    ) {
        val maxValue = dataPoints.max().toFloat()
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

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun Preview() {
    Mdc3Theme {
        Card {
            DepthChart(
                modifier = Modifier.height(64.dp),
                profile = DiveProfile.sample,
            )
        }
    }
}