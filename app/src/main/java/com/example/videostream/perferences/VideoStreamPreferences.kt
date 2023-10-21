package com.example.videostream.perferences

import android.content.SharedPreferences
import javax.inject.Inject

class VideoStreamPreferences @Inject constructor(private val preferences: SharedPreferences) {

    companion object {
        private const val DISPLAY_NAME = "DISPLAY_NAME"
    }

    var displayName: String?
        get() = preferences.getString(DISPLAY_NAME, null)
        set(value) = preferences.edit().putString(DISPLAY_NAME, value).apply()

    fun clearAll() {
        displayName = null
    }

}