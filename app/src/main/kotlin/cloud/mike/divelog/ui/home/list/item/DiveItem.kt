package cloud.mike.divelog.ui.home.list.item

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ScubaDiving
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cloud.mike.divelog.data.dives.Dive
import com.google.accompanist.themeadapter.material3.Mdc3Theme

@Composable
fun DiveItem(
    modifier: Modifier = Modifier,
    dive: Dive,
    onClick: () -> Unit,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
    ) {
        Box {
            Box(modifier = Modifier.height(64.dp)) {
                when (val profile = dive.diveProfile) {
                    null -> EmptyState(
                        modifier = Modifier.align(Alignment.Center),
                    )

                    else -> DepthChart(
                        profile = profile,
                    )
                }
            }
            Surface(
                modifier = Modifier.padding(8.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                shape = MaterialTheme.shapes.extraSmall,
            ) {
                Text(
                    modifier = Modifier.padding(4.dp),
                    text = dive.location,
                )
            }
        }
    }
}

@Composable
private fun EmptyState(
    modifier: Modifier = Modifier,
) {
    Icon(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        imageVector = Icons.Default.ScubaDiving,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.onSurface,
    )
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun PreviewNoProfile() {
    Mdc3Theme {
        DiveItem(
            dive = Dive.sample.copy(diveProfile = null),
            onClick = {},
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun Preview() {
    Mdc3Theme {
        DiveItem(
            dive = Dive.sample,
            onClick = {},
        )
    }
}
