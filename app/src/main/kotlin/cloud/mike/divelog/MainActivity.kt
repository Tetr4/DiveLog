package cloud.mike.divelog

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import cloud.mike.divelog.ui.DiveTheme
import cloud.mike.divelog.ui.NavRoot

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        // https://developer.android.com/develop/ui/views/layout/edge-to-edge
        // https://developer.android.com/jetpack/compose/layouts/insets
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            DiveTheme {
                NavRoot()
            }
        }
    }
}
