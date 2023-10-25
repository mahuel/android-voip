package com.example.videostream.presentation.viewmodel

import android.app.Application
import android.content.Intent
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.videostream.domain.model.Contact
import com.example.videostream.perferences.VideoStreamPreferences
import com.example.videostream.repository.ContactRepository
import com.example.videostream.service.NetworkMessagingService
import com.example.videostream.utils.getIpAddress
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ContactListViewModel @Inject constructor(
    private val videoStreamPreferences: VideoStreamPreferences,
    private val application: Application,
    private val contactRepository: ContactRepository
) : AndroidViewModel(application) {

    fun getDisplayName(): String? {
        return videoStreamPreferences.displayName
    }

    fun signOut() {
        videoStreamPreferences.clearAll()
        contactRepository.signOut()
    }

    fun getAddress(): String? {
        return application.getIpAddress()?.hostAddress
    }

    fun getContacts(): LiveData<MutableList<Contact>> {
        return contactRepository.getContactListLiveData()
    }

    fun startMessagingService() {
        application.applicationContext.apply {
            val intent2 = Intent(this, NetworkMessagingService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent2)
            } else {
                startService(intent2)
            }
        }
    }

    fun stopMessagingService() {
        application.applicationContext.apply {
            stopService(Intent(this, NetworkMessagingService::class.java))
        }
    }
}