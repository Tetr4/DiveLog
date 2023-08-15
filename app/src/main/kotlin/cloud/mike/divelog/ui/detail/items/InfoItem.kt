package cloud.mike.divelog.ui.detail.items

import android.content.res.Configuration
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import cloud.mike.divelog.data.dives.Dive
import cloud.mike.divelog.localization.format
import cloud.mike.divelog.ui.DiveTheme
import java.time.LocalDateTime
import kotlin.time.Duration

@Composable
fun InfoItem(
    start: LocalDateTime,
    diveTime: Duration,
    modifier: Modifier = Modifier,
) {
    Card(modifier = modifier.fillMaxWidth()) {
        ListItem(
            colors = ListItemDefaults.colors(
                containerColor = Color.Transparent,
            ),
            leadingContent = { Icon(Icons.Default.Event, contentDescription = null) },
            headlineContent = { Text(start.format()) },
            supportingContent = { Text(diveTime.format()) },
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun Preview() {
    DiveTheme {
        InfoItem(
            start = Dive.sample.start,
            diveTime = Dive.sample.diveTime,
        )
    }
}
