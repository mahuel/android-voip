package com.example.videostream.service

import android.util.Log
import com.example.videostream.domain.repository.CallRepository
import com.example.videostream.domain.repository.ContactRepository
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException
import java.net.InetAddress

class TcpMessageHandler(
    private val dataInputStream: DataInputStream,
    private val dataOutputStream: DataOutputStream,
    private val contactRepository: ContactRepository,
    private val callRepository: CallRepository,
    private val clientAddress: InetAddress
) : Thread() {

    companion object {
        const val GET_CONTACT_DETAILS = "GET/DETAILS"
        const val REMOVE_CONTACT = "REMOVE/CONTACT"
        const val CONNECT_CALL = "CONNECT/CALL"
        const val START_CALL = "START/CALL"
        const val STOP_CALL = "STOP/CALL"
    }

    override fun run() {
        while (true) {
            try {
                if (dataInputStream.available() > 0) {
                    val requestInput = dataInputStream.readUTF()
                    Log.e("TEST123", "requestInput:$requestInput")
                    if (requestInput.startsWith(GET_CONTACT_DETAILS)) {
                        contactRepository.detailsRequest()?.let {
                            dataOutputStream.writeUTF(it)
                        }
                    }

                    if (requestInput.startsWith(REMOVE_CONTACT)) {
                        dataOutputStream.writeUTF("ok")
                        contactRepository.removeAddress(clientAddress)
                    }

                    if (requestInput.startsWith(START_CALL)) {
                        dataOutputStream.writeUTF("ok")
                        callRepository.startCallRequested(clientAddress)
                    }

                    if (requestInput.startsWith(CONNECT_CALL)) {
                        dataOutputStream.writeUTF("ok")
                        callRepository.connectCallRequested(clientAddress)
                    }

                    if (requestInput.startsWith(STOP_CALL)) {
                        dataOutputStream.writeUTF("ok")
                        callRepository.stopCallRequested(clientAddress)
                    }

                    sleep(2000L)
                }
            } catch (e: IOException) {
                e.printStackTrace()
                try {
                    dataInputStream.close()
                    dataOutputStream.close()
                } catch (ex: IOException) {
                    ex.printStackTrace()
                }
            } catch (e: InterruptedException) {
                e.printStackTrace()
                try {
                    dataInputStream.close()
                    dataOutputStream.close()
                } catch (ex: IOException) {
                    ex.printStackTrace()
                }
            }
        }
    }

}