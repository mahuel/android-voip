package com.example.videostream.perferences

interface IVideoStreamPreferences {

    fun getDisplayName(): String?

    fun setDisplayName(displayName: String)

    fun clearAll()
}