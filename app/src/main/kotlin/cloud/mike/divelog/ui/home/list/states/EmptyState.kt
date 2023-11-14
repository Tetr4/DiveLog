package cloud.mike.divelog.ui.home.list.states

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cloud.mike.divelog.R
import cloud.mike.divelog.ui.DiveTheme
import cloud.mike.divelog.ui.spacing

private const val MIN_HEIGHT_FOR_IMAGE = 300

@Composable
fun EmptyState(modifier: Modifier = Modifier) {
    BoxWithConstraints {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(MaterialTheme.spacing.screenPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (this@BoxWithConstraints.maxHeight >= MIN_HEIGHT_FOR_IMAGE.dp) {
                Image(
                    modifier = Modifier
                        .weight(3f)
                        .fillMaxWidth(),
                    painter = painterResource(R.drawable.ic_launcher_monochrome),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground),
                )
            } else {
                Spacer(Modifier.weight(1f))
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(R.string.home_label_empty_title),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Text(
                    text = stringResource(R.string.home_label_empty_description),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }
            Spacer(Modifier.weight(1f))
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, heightDp = MIN_HEIGHT_FOR_IMAGE - 10)
@Preview(showBackground = true, heightDp = MIN_HEIGHT_FOR_IMAGE + 10)
@Composable
private fun Preview() {
    DiveTheme {
        EmptyState()
    }
}
