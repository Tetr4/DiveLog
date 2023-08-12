package cloud.mike.divelog.ui

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.accompanist.themeadapter.material3.Mdc3Theme

@Composable
fun DiveTheme(
    content: @Composable () -> Unit,
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        // Use user wallpaper colors (Material You)
        val context = LocalContext.current
        val colors = if (isSystemInDarkTheme()) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        MaterialTheme(
            colorScheme = colors,
            content = content,
        )
    } else {
        Mdc3Theme(content = content)
    }
}

@Immutable
data class Spacing(
    val cardPadding: PaddingValues,
)

@Suppress("UnusedReceiverParameter")
val MaterialTheme.spacing
    get() = Spacing(
        cardPadding = PaddingValues(16.dp),
    )