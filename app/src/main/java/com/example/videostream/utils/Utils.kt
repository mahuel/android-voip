package com.example.videostream.utils

import android.content.Context
import android.net.ConnectivityManager
import java.net.Inet4Address
import java.net.InetAddress


class Utils {

    fun getIpAddresses(context: Context): InetAddress?{
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val linkProperties = connectivityManager.getLinkProperties(connectivityManager.activeNetwork)
        linkProperties?.linkAddresses?.forEach {
            if (it.address is Inet4Address) {
                return it.address
            }
        }
        return null
    }
}