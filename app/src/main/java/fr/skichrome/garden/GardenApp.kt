package fr.skichrome.garden

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber

@Suppress("unused")
class GardenApp : Application()
{
    override fun onCreate()
    {
        super.onCreate()
        if (BuildConfig.DEBUG)
            Timber.plant(Timber.DebugTree())
    }

    private fun initKoin()
    {
        startKoin {
            androidLogger()
            androidContext(this@GardenApp)
            modules(getKoinModules())
        }
    }
}