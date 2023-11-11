package cloud.mike.divelog

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import cloud.mike.divelog.ui.DiveTheme
import cloud.mike.divelog.ui.NavRoot
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        // Draw edge to edge: https://developer.android.com/jetpack/compose/layouts/insets
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            DiveTheme {
                NavRoot()
            }
        }
    }
}
