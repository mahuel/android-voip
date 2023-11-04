package com.example.videostream.presentation.adapter

import com.example.videostream.domain.model.Contact

interface ContactListAdapterListener {
    fun onCallPressed(contact: Contact)
}