package cloud.mike.divelog

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import cloud.mike.divelog.ui.DiveTheme
import cloud.mike.divelog.ui.NavRoot

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContent {
            DiveTheme {
                NavRoot()
            }
        }
    }
}
