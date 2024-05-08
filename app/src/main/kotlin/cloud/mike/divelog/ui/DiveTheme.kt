package cloud.mike.divelog.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.accompanist.themeadapter.material3.Mdc3Theme

@Composable
fun DiveTheme(content: @Composable () -> Unit) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
        Mdc3Theme(content = content)
    } else {
        // Use colors based on user's wallpaper ("Material You")
        MaterialTheme(colorScheme = dynamicColorScheme()) {
            Mdc3Theme(
                readColorScheme = false,
                content = content,
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.S)
@Composable
@ReadOnlyComposable
private fun dynamicColorScheme(): ColorScheme {
    val context = LocalContext.current
    return if (isSystemInDarkTheme()) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
}

@Immutable
data class Spacing(
    val cardPadding: Dp,
    val screenPadding: Dp,
    val sheetPadding: Dp,
    val dialogPadding: Dp,
)

@Suppress("UnusedReceiverParameter")
val MaterialTheme.spacing
    get() = Spacing(
        cardPadding = 16.dp,
        screenPadding = 16.dp,
        sheetPadding = 16.dp,
        dialogPadding = 24.dp,
    )
