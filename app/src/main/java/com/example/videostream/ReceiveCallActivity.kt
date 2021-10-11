package com.example.videostream

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.videostream.databinding.ActivityReceiveCallBinding
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.SocketTimeoutException
import kotlin.concurrent.thread

class ReceiveCallActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReceiveCallBinding

    private var displayName: String? = null
    private var contactName: String? = null
    private var contactIp: String? = null
    private var listen = true
    private var inCall = false
    private var call: AudioCall? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReceiveCallBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        displayName = intent.getStringExtra(MainActivity.EXTRA_DISPLAYNAME)
        contactName = intent.getStringExtra(MainActivity.EXTRA_CONTACT)
        contactIp = intent.getStringExtra(MainActivity.EXTRA_IP)

        binding.textViewIncomingCall.text = "Incoming Call:$displayName"
        binding.buttonEndCall1.visibility = View.INVISIBLE

        startListening()

        binding.buttonAccept.setOnClickListener {
            sendMessage("ACC:", PORT)
            thread(start = true) {
                Log.d("TEST123", "accepted call")
                val address = InetAddress.getByName(contactIp)
                call = AudioCall(address)
                call!!.startCall()
            }
            binding.buttonAccept.isEnabled = false
            binding.buttonReject.isEnabled = false

            binding.buttonEndCall1.visibility = View.VISIBLE
        }

        binding.buttonReject.setOnClickListener {
            sendMessage("REJ:", PORT)
            endCall()
        }

        binding.buttonEndCall1.setOnClickListener {
            endCall()
        }

    }

    private fun startListening() {
        listen = true
        thread(start = true) {
            val datagramSocket = DatagramSocket(PORT)
            try {
                val buf = ByteArray(BUF_SIZE)
                val datagramPacket = DatagramPacket(buf, BUF_SIZE)
                while (listen) {
                    datagramSocket.soTimeout = 15000
                    datagramSocket.receive(datagramPacket)
                    val message = String(buf, 0, datagramPacket.length)
                    if (message.startsWith("END:")) {
                        endCall()
                    }
                }
                datagramSocket.disconnect()
                datagramSocket.close()
            } catch (e:SocketTimeoutException) {
                datagramSocket.disconnect()
                datagramSocket.close()
            } finally {
                if (listen)
                    startListening()
            }
        }
    }

    private fun endCall() {
        stopListening()
        if (inCall) {
            call!!.endCall()
        }
        sendMessage("END:", PORT)
        finish()
    }

    private fun sendMessage(message: String, port: Int) {
        thread(start = true) {
            val address = InetAddress.getByName(contactIp)
            val data = message.toByteArray()
            val datagramSocket = DatagramSocket()
            val datagramPacket = DatagramPacket(data, data.size, address, port)
            datagramSocket.send(datagramPacket)
            datagramSocket.disconnect()
            datagramSocket.close()
        }
    }

    private fun stopListening() {
        listen = false
    }

    companion object {
        private const val PORT = 50002 // Socket on which packets are sent/received
        private const val BUF_SIZE = 1024
    }
}