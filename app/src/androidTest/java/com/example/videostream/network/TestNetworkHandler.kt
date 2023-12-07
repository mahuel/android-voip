package com.example.videostream.network

import android.util.Log
import com.example.videostream.service.TcpMessageHandler.Companion.CONNECT_CALL
import com.example.videostream.service.TcpMessageHandler.Companion.GET_CONTACT_DETAILS
import com.example.videostream.service.TcpMessageHandler.Companion.REMOVE_CONTACT
import com.example.videostream.service.TcpMessageHandler.Companion.START_CALL
import com.example.videostream.service.TcpMessageHandler.Companion.STOP_CALL
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException
import java.net.InetAddress
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.atomic.AtomicBoolean

class TestNetworkHandler {

    private var serverSocket: ServerSocket? = null
    private val working = AtomicBoolean(true)
    fun makeRequest(address: InetAddress, request: String, port: Int): String? {

        val contactSocket = Socket(
            address,
            port
        )

        val bufferOutput = DataOutputStream(
            contactSocket.getOutputStream()
        )

        val bufferInput = DataInputStream(
            contactSocket.getInputStream()
        )

        bufferOutput.writeUTF(request)
        var output: String? = ""
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

    fun startListening(port: Int) {
        var socket: Socket? = null
        try {
            serverSocket = ServerSocket(port)
            while (working.get()) {
                if (serverSocket != null) {
                    Log.e("TEST123", "serversocket not null")
                    socket = serverSocket!!.accept()

                    val dataInputStream = DataInputStream(socket.getInputStream())
                    val dataOutputStream = DataOutputStream(socket.getOutputStream())


                    // Use threads for each client to communicate with them simultaneously
                    val t: Thread = TestMessageHandler(
                        dataInputStream = dataInputStream,
                        dataOutputStream = dataOutputStream,
                    )
                    t.start()
                } else {
                    Log.d("TEST123", "serversocket null")
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


    inner class TestMessageHandler(
        private val dataInputStream: DataInputStream,
        private val dataOutputStream: DataOutputStream
    ) : Thread() {

        override fun run() {
            while (true) {
                try {
                    if (dataInputStream.available() > 0) {
                        val requestInput = dataInputStream.readUTF()
                        Log.e("TEST123", "requestInput:$requestInput")
                        if (requestInput.startsWith(GET_CONTACT_DETAILS)) {
                            dataOutputStream.writeUTF("TestUser")
                        }

                        if (requestInput.startsWith(REMOVE_CONTACT)) {
                            dataOutputStream.writeUTF("ok")
                        }

                        if (requestInput.startsWith(START_CALL)) {
                            dataOutputStream.writeUTF("ok")
                        }

                        if (requestInput.startsWith(CONNECT_CALL)) {
                            dataOutputStream.writeUTF("ok")
                        }

                        if (requestInput.startsWith(STOP_CALL)) {
                            dataOutputStream.writeUTF("ok")
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

}