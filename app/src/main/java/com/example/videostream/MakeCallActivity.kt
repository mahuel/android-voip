package com.example.videostream

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.videostream.databinding.ActivityMakeCallBinding
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.SocketTimeoutException
import kotlin.concurrent.thread

class MakeCallActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMakeCallBinding

    private var displayName: String? = null
    private var contactName: String? = null
    private var contactIp: String? = null
    private var listen = true
    private var inCall = false
    private var call: AudioCall? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMakeCallBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        Log.d("TEST123", "MakeCallActivity started")

        displayName = intent.getStringExtra(MainActivity.EXTRA_DISPLAYNAME)
        contactName = intent.getStringExtra(MainActivity.EXTRA_CONTACT)
        contactIp = intent.getStringExtra(MainActivity.EXTRA_IP)
        Log.d("TEST123", "contact ip:$contactIp")
        binding.textViewCalling.text = "Calling: $displayName"

        startListening()
        makeCall()

        binding.buttonEndCall.setOnClickListener {
            endCall()
        }

        Log.d("TEST123", "MakeCallActivity on create ended")
    }

    private fun startListening() {
        listen = true
        thread(start = true) {
            val datagramSocket = DatagramSocket(PORT)
            try {
                datagramSocket.soTimeout = 15000
                val buf = ByteArray(BUF_SIZE)
                val datagramPacket = DatagramPacket(buf, BUF_SIZE)
                while (listen) {
                    datagramSocket.receive(datagramPacket)
                    Log.d("TEST123", "receiving packet")
                    if (datagramPacket.length == 0) {
                        Log.d("TEST123", "empty packet")
                        continue
                    }
                    val message = String(buf, 0, BUF_SIZE)
                    Log.d("TEST123", "MakeCall message:$message")

                    if (message.startsWith("ACC:")) {
                        Log.d("TEST123", "call accepted")
                        call = AudioCall(datagramPacket.address)
                        call!!.startCall()
                        inCall = true
                        this@MakeCallActivity.runOnUiThread {
                            binding.textViewCalling.text = "In call with $displayName"
                        }
                        continue
                    }

                    if (message.startsWith("REJ:")) {
                        endCall()
                        continue
                    }

                    if (message.startsWith("END:")) {
                        endCall()
                        continue
                    }
                }
                datagramSocket.disconnect()
                datagramSocket.close()
            } catch (e: SocketTimeoutException) {
                datagramSocket.disconnect()
                datagramSocket.close()
            } finally {
                if (listen)
                    startListening()
            }
        }
    }

    private fun stopListening() {
        listen = false
    }

    private fun sendMessage(message: String, port: Int) {
        thread(start = true) {
            Log.d("TEST123", "sending message: $message")
            val address = InetAddress.getByName(contactIp)
            val data = message.toByteArray()
            val datagramSocket = DatagramSocket()
            val datagramPacket = DatagramPacket(data, data.size, address, port)
            datagramSocket.send(datagramPacket)
            Log.d("TEST123", "message sent")
            datagramSocket.disconnect()
            datagramSocket.close()
        }
    }

    private fun endCall() {
        stopListening()
        if (inCall) {
            call!!.endCall()
            inCall = false
        }
        sendMessage("END:", PORT)
        finish()
    }

    private fun makeCall() {
        sendMessage("CAL:$displayName", 50003)
    }

    companion object {
        private const val PORT = 50002 // Socket on which packets are sent/received
        private const val BUF_SIZE = 1024
    }
}