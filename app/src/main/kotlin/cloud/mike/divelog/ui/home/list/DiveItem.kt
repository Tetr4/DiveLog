package cloud.mike.divelog.ui.home.list

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ScubaDiving
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
        modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
    ) {
        Box(Modifier.padding(16.dp)) {
            Text(dive.location)
            Icon(
                modifier = Modifier
                    .height(64.dp)
                    .padding(16.dp)
                    .fillMaxWidth(),
                imageVector = Icons.Default.ScubaDiving,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun Preview() {
    Mdc3Theme {
        DiveItem(dive = Dive.sample, onClick = {})
    }
}
