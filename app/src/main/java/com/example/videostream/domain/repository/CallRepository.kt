package com.example.videostream.domain.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.videostream.domain.model.CallState
import com.example.videostream.domain.model.CallStatus
import com.example.videostream.domain.model.Contact
import java.net.InetAddress
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CallRepository @Inject constructor(
    private val contactRepository: ContactRepository
) {
    private val callStatus: MutableLiveData<CallStatus> = MutableLiveData()
    private val outgoingCallLiveData: MutableLiveData<Contact> = MutableLiveData()
    private val connectCallLiveData: MutableLiveData<InetAddress> = MutableLiveData()
    private val incomingCallLiveData: MutableLiveData<Contact> = MutableLiveData()
    private val microphoneEnableStatus: MutableLiveData<Boolean> = MutableLiveData()
    private val callStopLiveData: MutableLiveData<Contact> = MutableLiveData()
    fun getCallStatus(): LiveData<CallStatus> {
        return callStatus
    }

    // Outgoing
    fun startOutgoingCall(address: InetAddress) {
        callStatus.value?.also {
            when (it.status) {
                CallState.STARTED -> {
                    if (address != it.contact.address) {
                        // TODO - send busy signal
                    }
                }

                CallState.STOPPED -> {
                    outgoingCall(address)
                }

                CallState.INCOMING -> {
                    if (address != it.contact.address) {
                        // TODO - send busy signal
                    }
                }

                CallState.OUTGOING -> {
                    if (address != it.contact.address) {
                        // TODO - send busy signal
                    }
                }
            }
        } ?: run {
            outgoingCall(address)
        }
    }

    private fun outgoingCall(address: InetAddress) {
        val contact = contactRepository.getContactByAddress(address)
        contact?.let {
            outgoingCallLiveData.postValue(it)
            callStatus.postValue(
                CallStatus(
                    status = CallState.OUTGOING,
                    contact = it
                )
            )
        }
    }

    fun getOutgoingCallLiveData(): LiveData<Contact> {
        return outgoingCallLiveData
    }


    // Incoming
    fun getIncomingCallLiveData(): LiveData<Contact> {
        return incomingCallLiveData
    }

    fun startCallRequested(clientAddress: InetAddress) {
        callStatus.value?.also {
            when (it.status) {
                CallState.STARTED -> {
                    if (clientAddress != it.contact.address) {
                        // TODO - send busy signal
                    }
                }

                CallState.STOPPED -> {
                    incomingCall(clientAddress)
                }

                CallState.INCOMING -> {
                    if (clientAddress != it.contact.address) {
                        // TODO - send busy signal
                    }
                }

                CallState.OUTGOING -> {
                    if (clientAddress != it.contact.address) {
                        // TODO - send busy signal
                    }
                }
            }
        } ?: run {
            incomingCall(clientAddress)
        }
    }

    private fun incomingCall(clientAddress: InetAddress) {
        val contact = contactRepository.getContactByAddress(clientAddress)
        contact?.let {
            incomingCallLiveData.postValue(it)
            callStatus.postValue(
                CallStatus(
                    status = CallState.INCOMING,
                    contact = it
                )
            )
        }
    }


    // connect outgoing
    fun connectCallRequested(clientAddress: InetAddress) {
        callStatus.value?.let {
            if (clientAddress == it.contact.address) {
                callStatus.postValue(
                    CallStatus(
                        status = CallState.STARTED,
                        contact = it.contact
                    )
                )
            }
        }
    }

    // connect incoming
    fun connectCall(address: InetAddress) {
        callStatus.value?.let {
            if (address == it.contact.address) {
                connectCallLiveData.postValue(address)
                callStatus.postValue(
                    CallStatus(
                        status = CallState.STARTED,
                        contact = it.contact
                    )
                )
            }
        }
    }

    fun getConnectCallLiveData(): LiveData<InetAddress> {
        return connectCallLiveData
    }


    // Microphone
    fun getMicrophoneEnableStatus(): LiveData<Boolean> {
        return microphoneEnableStatus
    }

    fun getMicrophoneEnabled(): Boolean {
        microphoneEnableStatus.value?.let {
            return it
        } ?: run {
            microphoneEnableStatus.value = false
            return false
        }
    }

    fun setMicrophoneEnabled(enable: Boolean) {
        microphoneEnableStatus.postValue(enable)
    }

    // Stop
    fun stopCall() {
        callStatus.value?.let {
            callStopLiveData.postValue(it.contact)
            callStatus.postValue(
                CallStatus(
                    status = CallState.STOPPED,
                    contact = it.contact
                )
            )
        }
    }

    fun getStopCallLiveData(): LiveData<Contact> {
        return callStopLiveData
    }

    fun stopCallRequested(address: InetAddress) {
        callStatus.value?.let {
            if (it.contact.address == address) {
                callStatus.postValue(
                    CallStatus(
                        status = CallState.STOPPED,
                        contact = it.contact
                    )
                )
            }
        }
    }
}