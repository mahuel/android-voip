package com.example.videostream

import android.content.Intent
import android.net.wifi.WifiManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import com.example.videostream.databinding.ActivityMainBinding
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.SocketTimeoutException
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var contactManager: ContactManager? = null
    private var displayName: String = "Name"

    private var listen = true
    private var inCall = false
    private var started = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.buttonStart.setOnClickListener {
            started = true
            displayName = binding.editTextDisplayName.text.toString()

            binding.editTextDisplayName.isEnabled = false
            binding.buttonStart.isEnabled = false

            binding.textViewSelectContact.visibility = View.VISIBLE
            binding.buttonUpdate.visibility = View.VISIBLE
            binding.buttonCall.visibility = View.VISIBLE
            binding.scrollView.visibility = View.VISIBLE

            contactManager = ContactManager(displayName, getBroadcastIp())
            startCallListener()
        }

        binding.buttonUpdate.setOnClickListener {
            updateContactList()
        }

        binding.buttonCall.setOnClickListener {
            val selectedButton = binding.contactList.checkedRadioButtonId

            if (selectedButton == -1)
                return@setOnClickListener

            val radioButton = findViewById<RadioButton>(selectedButton)
            val contact = radioButton.text.toString()
            val ip = contactManager?.getContacts()?.get(contact)
            Log.d("TEST123", "Got ip of contact")
            Log.d("TEST123", "hostAddress:${ip!!.hostAddress}")
            inCall = true

            val intent = Intent(this, MakeCallActivity::class.java)
            intent.putExtra(EXTRA_CONTACT, contact)
            intent.putExtra(EXTRA_IP, ip.hostAddress)
            intent.putExtra(EXTRA_DISPLAYNAME, displayName)
            startActivity(intent)
            finish()
        }
    }

    override fun onPause() {
        super.onPause()
        if (started) {
            contactManager!!.bye(displayName)
            contactManager!!.stopBroadcasting()
            contactManager!!.stopListening()
        }
        stopCallListener()
    }

    override fun onStop() {
        super.onStop()
        stopCallListener()
        if (!inCall) {
            finish()
        }
    }

    override fun onRestart() {
        super.onRestart()
        inCall = false
        started = true
        contactManager = ContactManager(displayName, getBroadcastIp())
        startCallListener()
    }

    private fun startCallListener() {
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

                    if (message.startsWith("CAL:")) {
                        val address = datagramPacket.address.hostAddress
                        val name = message.substring(4)
                        val intent = Intent(this, ReceiveCallActivity::class.java)
                        intent.putExtra(EXTRA_CONTACT, name)
                        intent.putExtra(EXTRA_IP, address)
                        inCall = true
                        startActivity(intent)
                    }
                }
                datagramSocket.disconnect()
                datagramSocket.close()
            } catch (e:SocketTimeoutException) {
                datagramSocket.disconnect()
                datagramSocket.close()
            } finally {
                if (listen)
                    startCallListener()
            }
        }
    }

    private fun stopCallListener() {
        listen = false
    }

    private fun getBroadcastIp(): InetAddress {
        val wifiManager: WifiManager = applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
        val connectionInfo = wifiManager.connectionInfo
        val stringAddress = toBroadcastIp(connectionInfo.ipAddress)
        return InetAddress.getByName(stringAddress)
    }

    private fun toBroadcastIp(ip: Int): String? {
        // Returns converts an IP address in int format to a formatted string
        return (ip and 0xFF).toString() + "." +
                (ip shr 8 and 0xFF) + "." +
                (ip shr 16 and 0xFF) + "." +
                "255"
    }

    private fun updateContactList() {
        val contacts = contactManager?.getContacts()
        binding.contactList.removeAllViews()

        for (name: String in contacts?.keys!!) {
            val radioButton = RadioButton(this)
            radioButton.text = name

            binding.contactList.addView(radioButton)
        }

        binding.contactList.clearCheck()
    }


    companion object {
        const val EXTRA_CONTACT = "hw.dt83.udpchat.CONTACT"
        const val EXTRA_IP = "hw.dt83.udpchat.IP"
        const val EXTRA_DISPLAYNAME = "hw.dt83.udpchat.DISPLAYNAME"

        private const val PORT = 50003
        private const val BUF_SIZE = 1024
    }
}
