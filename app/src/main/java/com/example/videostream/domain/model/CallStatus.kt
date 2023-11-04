package com.example.videostream.domain.model

data class CallStatus(
    val status: CallState,
    val contact: Contact
)

enum class CallState {
    STARTED,
    INCOMING,
    OUTGOING,
    STOPPED,
}
