package com.example.videostream

import android.util.Log
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import kotlin.concurrent.thread

class ContactManager(name: String, private val broadcastIp: InetAddress) {

    private var broadcast = true

    private var listen = true

    private val contacts: HashMap<String, InetAddress> = HashMap()

    init {
        listen()
        broadcastName(name)
    }

    private fun addContact(name: String, address: InetAddress) {
        if (!contacts.containsKey(name)) {
            contacts[name] = address
        }
    }

    private fun removeContact(name: String) {
        if (contacts.containsKey(name)) {
            contacts.remove(name)
        }
    }

    fun getContacts() : HashMap<String, InetAddress> {
        return contacts
    }

    fun stopBroadcasting() {
        broadcast = false
    }

    fun stopListening() {
        listen = false
    }

    fun bye(name: String) {
        thread(start = true) {
            val message = "BYE:$name".toByteArray()
            val datagramSocket = DatagramSocket()
            datagramSocket.broadcast = true
            val datagramPacket = DatagramPacket(message, message.size, broadcastIp, PORT)
            datagramSocket.send(datagramPacket)
            datagramSocket.disconnect()
            datagramSocket.close()
        }
    }

    private fun broadcastName(name: String) {
        thread(start = true) {
            val message = "ADD:$name".toByteArray()
            val datagramSocket = DatagramSocket()
            datagramSocket.broadcast = true
            val datagramPacket = DatagramPacket(message, message.size, broadcastIp, PORT)
            while (broadcast) {
                datagramSocket.send(datagramPacket)
                Thread.sleep(BROADCAST_INTERVAL)
            }

            datagramSocket.disconnect()
            datagramSocket.close()

        }
    }

    private fun listen() {
        thread(start = true) {
            val datagramSocket = DatagramSocket(PORT)
            val buf = ByteArray(BUF_SIZE)
            val datagramPacket = DatagramPacket(buf, BUF_SIZE)
            while (listen) {
                datagramSocket.soTimeout = 15000
                datagramSocket.receive(datagramPacket)
                val message = String(buf, 0, datagramPacket.length)
                Log.d("TEST123", "message:$message")
                if (message.startsWith("ADD:")) {
                    addContact(message.substring(4), datagramPacket.address)
                    continue
                }

                if (message.startsWith("BYE:")) {
                    removeContact(message.substring(4))
                    continue
                }
            }
            datagramSocket.disconnect()
            datagramSocket.close()
        }
    }

    companion object {
        private const val PORT = 50001 // Socket on which packets are sent/received
        private const val BROADCAST_INTERVAL = 10000L // Milliseconds
        private const val BUF_SIZE = 1024
    }
}