package com.example.videostream.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.videostream.domain.model.Contact
import com.example.videostream.perferences.VideoStreamPreferences
import java.net.InetAddress
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ContactRepository @Inject constructor(
    private val videoStreamPreferences: VideoStreamPreferences
) {
    private val contactList: MutableList<Contact> = mutableListOf()

    private val contactListLiveData: MutableLiveData<MutableList<Contact>> = MutableLiveData()
    private val newAddressLiveData: MutableLiveData<InetAddress> = MutableLiveData()
    private val signOutLiveData: MutableLiveData<List<Contact>> = MutableLiveData()

    fun addAddress(address: InetAddress) {
        val hasAddressAlready = contactList.find {
            address == it.address
        }

        if (hasAddressAlready == null) {
            newAddressLiveData.postValue(address)
        }
    }

    fun removeAddress(address: InetAddress) {
        val hasAddressAlready = contactList.find {
            address == it.address
        }

        hasAddressAlready?.let {
            contactList.remove(hasAddressAlready)
            contactListLiveData.postValue(contactList)
        }
    }

    fun getSignOutLiveData(): LiveData<List<Contact>> {
        return signOutLiveData
    }

    fun getNewAddressLiveData(): LiveData<InetAddress> {
        return newAddressLiveData
    }

    fun getContactListLiveData(): LiveData<MutableList<Contact>> {
        return contactListLiveData
    }

    fun addContact(contact: Contact) {
        contactList.add(contact)
        contactListLiveData.postValue(contactList)
    }

    fun detailsRequest(): String? {
        return videoStreamPreferences.displayName
    }

    fun signOut() {
        signOutLiveData.postValue(contactList.toList())
        contactList.clear()
    }
}