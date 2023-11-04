package com.example.videostream.presentation.viewmodel

import android.Manifest
import android.app.Application
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.videostream.domain.model.CallStatus
import com.example.videostream.domain.model.Contact
import com.example.videostream.domain.repository.CallRepository
import com.example.videostream.service.CallService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.net.InetAddress
import javax.inject.Inject

@HiltViewModel
class CallViewModel @Inject constructor(
    private val callRepository: CallRepository,
    private val application: Application
) : AndroidViewModel(application) {

    private val microphoneEnable: MutableLiveData<Boolean> = MutableLiveData()
    fun getIncomingCalls(): LiveData<Contact> {
        return MediatorLiveData<Contact>().apply {
            viewModelScope.launch {
                addSource(callRepository.getIncomingCallLiveData()) {
                    startCallService()
                    value = it
                }
            }
        }
    }

    private fun startCallService() {

        application.applicationContext.apply {
            val intent2 = Intent(this, CallService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent2)
            } else {
                startService(intent2)
            }
        }
    }

    fun outgoingCall(address: InetAddress) {
        callRepository.startOutgoingCall(address)
        startCallService()
    }

    fun connectCall(address: InetAddress) {
        callRepository.connectCall(address)
    }

    fun getCallStatus(): LiveData<CallStatus> {
        return callRepository.getCallStatus()
    }

    fun getMicrophoneEnable(): LiveData<Boolean> {
        return microphoneEnable
    }

    fun microphoneToggle(requestPermissionLauncher: ActivityResultLauncher<String>) {
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(
                application.applicationContext,
                Manifest.permission.RECORD_AUDIO
            ) -> {
                val enabled = callRepository.getMicrophoneEnabled()
                microphoneEnable.value = !enabled
                callRepository.setMicrophoneEnabled(!enabled)
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        }
    }

    fun stopCall() {
        callRepository.stopCall()
    }

}