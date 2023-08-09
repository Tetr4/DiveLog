package cloud.mike.divelog.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.dp
import com.google.accompanist.themeadapter.material3.Mdc3Theme

@Composable
fun DiveTheme(
    content: @Composable () -> Unit,
) {
    Mdc3Theme(content = content)
}

@Immutable
data class Spacing(
    val cardPadding: PaddingValues,
)

val MaterialTheme.spacing
    get() = Spacing(
        cardPadding = PaddingValues(16.dp),
    )