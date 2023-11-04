package com.example.videostream.perferences

import android.content.SharedPreferences

class VideoStreamPreferences constructor(
    private val preferences: SharedPreferences
) : IVideoStreamPreferences {

    companion object {
        private const val DISPLAY_NAME = "DISPLAY_NAME"
    }

    private var mDisplayName: String?
        get() = preferences.getString(DISPLAY_NAME, null)
        set(value) = preferences.edit().putString(DISPLAY_NAME, value)
            .apply()

    override fun getDisplayName(): String? {
        return mDisplayName
    }

    override fun setDisplayName(displayName: String) {
        mDisplayName = displayName
    }

    override fun clearAll() {
        mDisplayName = null
    }

}