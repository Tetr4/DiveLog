package cloud.mike.divelog

import android.app.Application
import android.util.Log
import io.reactivex.plugins.RxJavaPlugins
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        RxJavaPlugins.setErrorHandler { Log.e("App", it.message, it) } // prevent crash on unhandled exceptions
        startKoin {
            androidContext(this@App)
            modules(appModule)
        }
    }
}
