package fr.skichrome.garden

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import timber.log.Timber

@Suppress("unused")
class GardenApp : Application()
{
    // ===================================
    //         Superclass Methods
    // ===================================

    override fun onCreate()
    {
        super.onCreate()
        initKoin()
        initTimber()
    }

    // ===================================
    //               Methods
    // ===================================

    private fun initKoin()
    {
        startKoin {
            // Waiting a fix from Koin (incompatibility with kotlin 1.6 https://github.com/InsertKoinIO/koin/issues/1188)
            androidLogger(if (BuildConfig.DEBUG) Level.ERROR else Level.NONE)
            androidContext(this@GardenApp)
            modules(getKoinModules())
        }
    }

    private fun initTimber()
    {
        if (BuildConfig.DEBUG)
            Timber.plant(Timber.DebugTree())
    }
}