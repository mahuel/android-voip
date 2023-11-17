package com.example.videostream.service

import javax.inject.Inject

class PortProvider @Inject constructor() {
    var listeningPort = 9876
    var broadcastPort = 9876

    fun getMessageListeningPort(): Int {
        return listeningPort
    }

    fun getMessageBroadcastPort(): Int {
        return broadcastPort
    }
}