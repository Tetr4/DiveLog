package cloud.mike.divelog.ui.home.list.states

import android.content.res.Configuration
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cloud.mike.divelog.localization.errors.ErrorMessage
import cloud.mike.divelog.ui.DiveTheme
import cloud.mike.divelog.ui.common.states.ErrorView
import cloud.mike.divelog.ui.spacing

@Composable
fun TrailingErrorState(
    message: ErrorMessage,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ErrorView(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = MaterialTheme.spacing.screenPadding,
                vertical = 16.dp,
            ),
        message = message,
        onRetry = onRetry,
    )
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun Preview() {
    DiveTheme {
        TrailingErrorState(
            message = ErrorMessage("Lorem Ipsum"),
            onRetry = {},
        )
    }
}
