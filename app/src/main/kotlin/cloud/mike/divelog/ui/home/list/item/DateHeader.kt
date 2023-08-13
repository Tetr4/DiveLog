package cloud.mike.divelog.ui.home.list.item

import android.content.res.Configuration
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cloud.mike.divelog.R
import cloud.mike.divelog.localization.format
import cloud.mike.divelog.ui.DiveTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit.MILLIS

@Composable
fun DateHeader(
    localDate: LocalDate,
    modifier: Modifier = Modifier,
) {
    val today = rememberToday()
    Surface(
        modifier = modifier.fillMaxWidth(),
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            text = if (localDate == today) {
                stringResource(R.string.home_date_header_label_today)
            } else {
                localDate.format()
            },
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun rememberToday(timeZone: ZoneId = ZoneId.systemDefault()): LocalDate {
    var today by remember(timeZone) { mutableStateOf(LocalDate.now(timeZone)) }
    LaunchedEffect(timeZone) {
        // Update after midnight
        while (isActive) {
            val tomorrow = today.plusDays(1)
            delay(LocalDateTime.now(timeZone).until(tomorrow.atStartOfDay(), MILLIS))
            today = tomorrow
        }
    }
    return today
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewToday() {
    DiveTheme {
        DateHeader(localDate = LocalDate.now())
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun Preview() {
    DiveTheme {
        DateHeader(localDate = LocalDate.now().minusDays(1))
    }
}
