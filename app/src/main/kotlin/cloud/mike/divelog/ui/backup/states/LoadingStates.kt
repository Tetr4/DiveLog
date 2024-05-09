package cloud.mike.divelog.ui.backup.states

import android.content.res.Configuration
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import cloud.mike.divelog.ui.DiveTheme
import cloud.mike.divelog.ui.spacing

@Composable
fun BackupProgressView(
    modifier: Modifier = Modifier,
) {
    CircularProgressIndicator(
        modifier = modifier.padding(MaterialTheme.spacing.sheetPadding),
    )
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun Preview() {
    DiveTheme {
        BackupProgressView()
    }
}
