package cloud.mike.divelog.ui.detail.items

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cloud.mike.divelog.R
import cloud.mike.divelog.data.dives.Dive
import cloud.mike.divelog.ui.DiveTheme
import cloud.mike.divelog.ui.common.card.CardHeadline
import cloud.mike.divelog.ui.spacing

@Composable
fun BuddyItem(
    buddy: String,
    modifier: Modifier = Modifier,
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(MaterialTheme.spacing.cardPadding)) {
            CardHeadline(
                imageVector = Icons.Filled.Group,
                title = stringResource(R.string.dive_detail_label_buddy),
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = buddy,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun Preview() {
    DiveTheme {
        BuddyItem(
            buddy = Dive.sample.buddy.orEmpty(),
        )
    }
}
