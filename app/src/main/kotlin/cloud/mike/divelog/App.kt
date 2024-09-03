package cloud.mike.divelog

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(appModule)
        }
    }

    companion object {
        // Feature flags
        const val SHOW_FILTERS = false
        const val SHOW_DIVE_SPOTS = false
    }
}
