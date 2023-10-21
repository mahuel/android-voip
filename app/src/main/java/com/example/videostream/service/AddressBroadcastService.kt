package com.example.videostream.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.videostream.R
import com.example.videostream.utils.Utils
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.concurrent.thread

class AddressBroadcastService : Service() {

    companion object {
        private const val PORT = 50001 // Socket on which packets are sent/received
        private const val BROADCAST_INTERVAL = 10000L // Milliseconds
        private const val BUF_SIZE = 1024
    }

    private val working = AtomicBoolean(true)

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        startMeForeground()
        val address = Utils().getIpAddresses(applicationContext)

        if (address != null) {
            broadcastHello(address)
        }

        listen()
    }

    override fun onDestroy() {
        working.set(false)
    }

    private fun broadcastHello(address: InetAddress) {
        thread(start = true) {
            val message = "HELLO:".toByteArray()
            val datagramSocket = DatagramSocket()
            datagramSocket.broadcast = true
            val datagramPacket = DatagramPacket(message, message.size, address, PORT)
            while (working.get()) {
                datagramSocket.send(datagramPacket)
                Thread.sleep(BROADCAST_INTERVAL)
            }

            val byeMessage = "BYE:".toByteArray()
            val byeDatagramPacket = DatagramPacket(
                byeMessage, byeMessage.size, address, PORT
            )
            datagramSocket.send(byeDatagramPacket)
            datagramSocket.disconnect()
            datagramSocket.close()
        }
    }

    private fun listen() {
        thread(start = true) {
            val datagramSocket = DatagramSocket(PORT)
            val buf = ByteArray(BUF_SIZE)
            val datagramPacket = DatagramPacket(buf, BUF_SIZE)
            while (working.get()) {
                datagramSocket.soTimeout = 15000
                datagramSocket.receive(datagramPacket)
                val message = String(buf, 0, datagramPacket.length)
                Log.d("TEST123", "message:$message")
                if (message.startsWith("HELLO:")) {
                    Log.d("TEST123", "found HELLO at ${datagramPacket.address.hostAddress}")

                    //addContact(message.substring(4), datagramPacket.address)
                    continue
                }

                if (message.startsWith("BYE:")) {
                    Log.d("TEST123", "found BYE at ${datagramPacket.address.hostAddress}")

//                    removeContact(message.substring(4))
                    continue
                }
            }
            datagramSocket.disconnect()
            datagramSocket.close()
        }
    }

    private fun startMeForeground() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val NOTIFICATION_CHANNEL_ID = packageName + "addressBroadcast"
            val channelName = "Address Broadcast Service"
            val chan = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                channelName,
                NotificationManager.IMPORTANCE_NONE
            )
//            chan.lightColor = Color.BLUE
//            chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            val manager = (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
            manager.createNotificationChannel(chan)
            val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            val notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("IP address is broadcasting")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build()
            startForeground(2, notification)
        } else {
            startForeground(1, Notification())
        }
    }
}