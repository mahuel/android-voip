package com.example.videostream.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import com.example.videostream.R
import com.example.videostream.domain.model.Contact
import com.example.videostream.domain.repository.CallRepository
import com.example.videostream.domain.repository.ContactRepository
import com.example.videostream.service.TcpMessageHandler.Companion.CONNECT_CALL
import com.example.videostream.service.TcpMessageHandler.Companion.GET_CONTACT_DETAILS
import com.example.videostream.service.TcpMessageHandler.Companion.REMOVE_CONTACT
import com.example.videostream.service.TcpMessageHandler.Companion.START_CALL
import com.example.videostream.service.TcpMessageHandler.Companion.STOP_CALL
import dagger.hilt.android.AndroidEntryPoint
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException
import java.net.InetAddress
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import kotlin.concurrent.thread

@AndroidEntryPoint
class NetworkMessagingService : LifecycleService() {

    companion object {
        private const val TAG = "TEST123"
    }

    @Inject
    lateinit var contactRepository: ContactRepository

    @Inject
    lateinit var callRepository: CallRepository

    @Inject
    lateinit var portProvider: PortProvider

    private val newAddresses: ArrayList<InetAddress> = ArrayList()

    private var serverSocket: ServerSocket? = null
    private val working = AtomicBoolean(true)
    private var requestContactWorking = false
    private val runnable = Runnable {
        var socket: Socket? = null
        try {
            serverSocket = ServerSocket(portProvider.getMessageListeningPort())
            while (working.get()) {
                if (serverSocket != null) {
                    socket = serverSocket!!.accept()
                    Log.i(TAG, "New client: $socket")
                    contactRepository.addAddress(socket.inetAddress)

                    val dataInputStream = DataInputStream(socket.getInputStream())
                    val dataOutputStream = DataOutputStream(socket.getOutputStream())

                    // Use threads for each client to communicate with them simultaneously
                    val t: Thread = TcpMessageHandler(
                        dataInputStream = dataInputStream,
                        dataOutputStream = dataOutputStream,
                        contactRepository = contactRepository,
                        callRepository = callRepository,
                        clientAddress = socket.inetAddress
                    )
                    t.start()
                } else {
                    Log.e(TAG, "Couldn't create ServerSocket!")
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            try {
                socket?.close()
            } catch (ex: IOException) {
                ex.printStackTrace()
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        startMeForeground()
        Thread(runnable).start()

        contactRepository.getNewAddressLiveData().observe(this) {
            Log.d(TAG, "getNewAddressLiveData")
            requestContactDetails(it)
        }

        contactRepository.getSignOutLiveData().observe(this) {
            Log.d(TAG, "getSignOutLiveData")
            requestRemoveContacts(it)
        }

        callRepository.getOutgoingCallLiveData().observe(this) {
            Log.d(TAG, "getOutgoingCallLiveData")
            requestCall(it)
        }

        callRepository.getConnectCallLiveData().observe(this) {
            Log.d(TAG, "getConnectCallLiveData")
            requestConnectCall(it)
        }

        callRepository.getStopCallLiveData().observe(this) {
            Log.d(TAG, "getStopCallLiveData")
            requestStopCall(it)
        }
    }

    private fun requestStopCall(it: Contact) {
        thread(start = true) {
            makeRequest(
                address = it.address,
                request = STOP_CALL,
            )
        }
    }

    private fun requestConnectCall(it: InetAddress) {
        thread(start = true) {
            makeRequest(
                address = it,
                request = CONNECT_CALL,
            )
        }
    }

    private fun requestCall(contact: Contact) {
        thread(start = true) {
            Log.e(TAG, "request call start:${contact.name} / ${contact.address.hostName}")
            makeRequest(
                address = contact.address,
                request = START_CALL,
            )
        }
    }

    private fun requestRemoveContacts(contacts: List<Contact>) {
        thread(start = true) {
            contacts.forEach {
                Log.d(TAG, "request remove:${it.name} / ${it.address.hostName}")
                makeRequest(
                    address = it.address,
                    request = REMOVE_CONTACT,
                )
            }
        }
    }

    private fun requestContactDetails(address: InetAddress) {
        newAddresses.add(address)
        thread(start = true) {
            if (requestContactWorking) {
                return@thread
            }

            requestContactWorking = true
            val addressesToQuery = newAddresses.toList()
            newAddresses.clear()
            addressesToQuery.forEach { queryAddress ->
                Log.d(TAG, "querying address:${queryAddress.hostAddress}")

                val contactName = makeRequest(
                    address = queryAddress,
                    request = GET_CONTACT_DETAILS,
                )

                contactName?.let {
                    contactRepository.addContact(
                        Contact(
                            name = it,
                            address = queryAddress
                        )
                    )
                }
            }
            requestContactWorking = false
        }
    }

    private fun makeRequest(address: InetAddress, request: String): String? {

        val contactSocket = Socket(
            address,
            portProvider.getMessageBroadcastPort()
        )

        val bufferOutput = DataOutputStream(
            contactSocket.getOutputStream()
        )

        val bufferInput = DataInputStream(
            contactSocket.getInputStream()
        )

        bufferOutput.writeUTF(request)
        var output:String? = ""
        var working = true
        while (working) {
            if (bufferInput.available() > 0) {
                output = bufferInput.readUTF()
                if (output.isNotEmpty()) {
                    working = false
                }
            }
        }
        bufferOutput.close()
        bufferInput.close()
        contactSocket.close()
        return output
    }

    override fun onDestroy() {
        super.onDestroy()
        working.set(false)
    }


    private fun startMeForeground() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val NOTIFICATION_CHANNEL_ID = packageName + "networkMessaging"
            val channelName = "Network Messaging Service"
            val chan = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                channelName,
                NotificationManager.IMPORTANCE_NONE
            )
            val manager = (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
            manager.createNotificationChannel(chan)
            val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            val notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Network messaging service is running")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build()
            startForeground(4, notification)
        } else {
            startForeground(3, Notification())
        }
    }
}