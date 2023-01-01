package com.gbversion.tool.statussaver.utils

import android.app.Service
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import com.gbversion.tool.statussaver.WAToolsApp

class NetworkState {
    companion object {
        fun isOnline(): Boolean {
            val cm =
                WAToolsApp.mInstance.getSystemService(Service.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val net = cm.activeNetwork ?: return false
                val actNw = cm.getNetworkCapabilities(net) ?: return false
                return when {
                    actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                    actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                    //for other device how are able to connect with Ethernet
                    actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                    //for check internet over Bluetooth
                    actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
                    else -> false
                }
            } else {
                return cm.activeNetworkInfo?.isConnected ?: false
            }
        }
    }
}