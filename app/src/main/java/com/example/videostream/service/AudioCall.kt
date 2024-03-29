package com.example.videostream.service

import android.Manifest
import android.annotation.SuppressLint
import android.media.*
import androidx.annotation.RequiresPermission
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.concurrent.thread

class AudioCall(private val inetAddress: InetAddress) {

    private var mic = AtomicBoolean(false)

    private val callStarted = AtomicBoolean(false)

    @SuppressLint("MissingPermission")
    fun startCall() {
        callStarted.set(true)
        startMic()
        startSpeakers()
    }

    fun endCall() {
        callStarted.set(false)
    }

    fun setMicStatus(enable: Boolean) {
        mic.set(enable)
    }

    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    private fun startMic() {
        thread(start = true) {
            while (callStarted.get()) {
                if (mic.get()) {
                    val audioRecorder = AudioRecord(
                        MediaRecorder.AudioSource.MIC,
                        SAMPLE_RATE,
                        AudioFormat.CHANNEL_IN_MONO,
                        AudioFormat.ENCODING_PCM_16BIT,
                        AudioRecord.getMinBufferSize(
                            SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT
                        ) * 10
                    )

                    var bytesRead: Int
                    var bytesSent = 0
                    val buf = ByteArray(BUF_SIZE)

                    val datagramSocket = DatagramSocket()
                    audioRecorder.startRecording()

                    while (mic.get()) {
                        bytesRead = audioRecorder.read(buf, 0, BUF_SIZE)
                        val datagramPacket = DatagramPacket(buf, 0, bytesRead, inetAddress, PORT)
                        datagramSocket.send(datagramPacket)
                        bytesSent += bytesRead
                        Thread.sleep(SAMPLE_INTERVAL.toLong(), 0)
                    }

                    audioRecorder.stop()
                    audioRecorder.release()
                    datagramSocket.disconnect()
                    datagramSocket.close()
                }
            }
        }
    }

    private fun startSpeakers() {
        thread(start = true) {
            val audioTrack = AudioTrack.Builder().setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH).build()
            ).setAudioFormat(
                AudioFormat.Builder().setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(SAMPLE_RATE)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            ).setBufferSizeInBytes(BUF_SIZE).setTransferMode(AudioTrack.MODE_STREAM)
                .build()
            audioTrack.play()

            val datagramSocket = DatagramSocket(PORT)
            val buf = ByteArray(BUF_SIZE)
            while (callStarted.get()) {
                val datagramPacket = DatagramPacket(buf, BUF_SIZE)
                datagramSocket.receive(datagramPacket)
                audioTrack.write(datagramPacket.data, 0, BUF_SIZE)
            }

            datagramSocket.disconnect()
            datagramSocket.close()

            audioTrack.stop()
            audioTrack.flush()
            audioTrack.release()
        }
    }

    companion object {
        private const val PORT = 50000 // Socket on which packets are sent/received
        private const val SAMPLE_RATE = 8000 // Hertz
        private const val SAMPLE_INTERVAL = 20 // Milliseconds
        private const val SAMPLE_SIZE = 2 // Bytes
        private const val BUF_SIZE = SAMPLE_INTERVAL * SAMPLE_INTERVAL * SAMPLE_SIZE * 2 //Bytes
    }
}