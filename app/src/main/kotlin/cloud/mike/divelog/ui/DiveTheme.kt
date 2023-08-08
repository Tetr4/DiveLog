package cloud.mike.divelog.ui

import androidx.compose.runtime.Composable
import com.google.accompanist.themeadapter.material3.Mdc3Theme

@Composable
fun DiveTheme(
    content: @Composable () -> Unit,
) {
    Mdc3Theme(content = content)
}