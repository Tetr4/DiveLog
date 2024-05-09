package cloud.mike.divelog.ui.backup

import android.content.Context
import android.content.Intent
import kotlin.system.exitProcess

fun Context.restartApp() {
    val intent = packageManager.getLaunchIntentForPackage(packageName) ?: error("App has no launcher intent")
    val mainIntent = Intent.makeRestartActivityTask(intent.component)
    startActivity(mainIntent)
    exitProcess(0)
}
