package com.example.videostream.service

import android.util.Log
import com.example.videostream.repository.ContactRepository
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException
import java.net.InetAddress

class TcpMessageHandler(
    private val dataInputStream: DataInputStream,
    private val dataOutputStream: DataOutputStream,
    private val contactRepository: ContactRepository,
    private val clientAddress: InetAddress
) : Thread() {

    companion object {
        const val GET_CONTACT_DETAILS = "GET/DETAILS"
        const val REMOVE_CONTACT = "REMOVE/CONTACT"
    }

    override fun run() {
        while (true) {
            try {
                if (dataInputStream.available() > 0) {
                    val requestInput = dataInputStream.readUTF()
                    Log.d("TEST123", "requestInput:$requestInput")
                    if (requestInput.startsWith(GET_CONTACT_DETAILS)) {
                        dataOutputStream.writeUTF(
                            contactRepository.detailsRequest()
                        )
                    }

                    if (requestInput.startsWith(REMOVE_CONTACT)) {
                        contactRepository.removeAddress(clientAddress)
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