package cloud.mike.divelog.ui.detail.items

import android.content.res.Configuration
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cloud.mike.divelog.data.dives.DepthProfile
import cloud.mike.divelog.ui.DiveTheme
import cloud.mike.divelog.ui.common.DepthChart

@Composable
fun ProfileItem(
    profile: DepthProfile,
    modifier: Modifier = Modifier,
) {
    Card(modifier = modifier.fillMaxWidth()) {
        // TODO label
        // TODO points of interest?
        DepthChart(
            modifier = Modifier.height(128.dp),
            profile = profile,
        )
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun Preview() {
    DiveTheme {
        ProfileItem(
            profile = DepthProfile.sample,
        )
    }
}
