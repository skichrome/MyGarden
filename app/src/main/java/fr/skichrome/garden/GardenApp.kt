package fr.skichrome.garden

import android.app.Application
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
}