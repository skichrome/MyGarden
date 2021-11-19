package fr.skichrome.garden.service

import android.content.Context
import androidx.startup.Initializer
import androidx.work.WorkManager

class AppLaunchInitializer: Initializer<WorkManager>
{
    override fun create(context: Context): WorkManager
    {
        TODO("Not yet implemented")
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>>
    {
        TODO("Not yet implemented")
    }
}