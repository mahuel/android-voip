package com.example.videostream.utils

import android.content.Context
import android.net.ConnectivityManager
import android.widget.Toast
import java.net.Inet4Address
import java.net.InetAddress

fun Context.makeToast(text: String) {
    Toast.makeText(
        this,
        text,
        Toast.LENGTH_LONG
    ).show()
}

fun Context.getIpAddress(): InetAddress? {
    val connectivityManager =
        getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val linkProperties =
        connectivityManager.getLinkProperties(connectivityManager.activeNetwork)
    linkProperties?.linkAddresses?.forEach {
        if (it.address is Inet4Address) {
            return it.address
        }
    }
    return null
}