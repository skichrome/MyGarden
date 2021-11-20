package fr.skichrome.garden.androidmanager

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build

interface NetManager
{
    val connManager: ConnectivityManager
    val isConnected: Boolean
}

class NetManagerImpl(app: Application) : NetManager
{
    override val connManager: ConnectivityManager = app.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    override val isConnected: Boolean
        get()
        {
            return if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M)
            {
                val activeNetwork = connManager.activeNetwork ?: return false
                val networkCapabilities = connManager.getNetworkCapabilities(activeNetwork) ?: return false

                when
                {
                    networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                    networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                    networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                    networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN) -> true
                    else -> false
                }
            } else
            {
                connManager.run {
                    @Suppress("DEPRECATION") // Deprecated in Api level 29, but only way to easily detect internet connection below Api level 23
                    activeNetworkInfo?.run {
                        isConnected
                    }
                } ?: false
            }
        }
}