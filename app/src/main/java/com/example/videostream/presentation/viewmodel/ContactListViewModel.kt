package com.example.videostream.presentation.viewmodel

import android.app.Application
import android.content.Intent
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import com.example.videostream.perferences.VideoStreamPreferences
import com.example.videostream.service.AddressBroadcastService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ContactListViewModel @Inject constructor(
    private val videoStreamPreferences: VideoStreamPreferences,
    private val application: Application
) : AndroidViewModel(application) {

    fun getDisplayName(): String? {
        return videoStreamPreferences.displayName
    }

    fun signOut() {
        videoStreamPreferences.clearAll()
    }

    fun startAddressBroadcastService() {
        application.applicationContext.apply {
            val intent = Intent(this, AddressBroadcastService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent)
            } else {
                startService(intent)
            }
        }

//        val address = Utils().getIpAddresses(application.applicationContext)
//
//        address?.let {
//            application.applicationContext.apply {
//                it.hostAddress?.let { ipAddress ->
//                    val intent = Intent(this, AddressBroadcastService::class.java)
//                    intent.putExtra(AddressBroadcastService.IP_ADDRESS, ipAddress)
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                        startForegroundService(intent)
//                    } else {
//                        startService(intent)
//                    }
//                }
//            }
//        }
    }

    fun stopAddressBroadcastService() {
        application.applicationContext.apply {
            stopService(Intent(this, AddressBroadcastService::class.java))
        }
    }
}