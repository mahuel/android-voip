package com.example.videostream.presentation.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.example.videostream.domain.repository.ContactRepository
import com.example.videostream.utils.Utils
import com.example.videostream.utils.getIpAddress
import dagger.hilt.android.lifecycle.HiltViewModel
import java.net.InetAddress
import javax.inject.Inject

@HiltViewModel
class AddContactViewModel @Inject constructor(
    private val application: Application,
    private val contactRepository: ContactRepository
) : AndroidViewModel(application) {

    fun getQRCodeBitmap(): Bitmap? {
        val host = application.getIpAddress()?.hostAddress
        host?.also {
            return Utils.createQrCodeAsBitmap(it)
        }

        return null
    }

    fun addAddress(address: String?): Boolean {
        address?.let {
            try{
                Log.d("TEST123", "Captured address:$address")
                val contactAddress = InetAddress.getByName(it)
                contactRepository.addAddress(contactAddress)
                return true
            } catch (e: Exception) {
                return false
            }
        }

        return false
    }
}