package com.example.videostream.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.example.videostream.perferences.VideoStreamPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LaunchViewModel @Inject constructor(
    private val videoStreamPreferences: VideoStreamPreferences
) : ViewModel() {

    fun isDisplayNameSet(): Boolean {
        videoStreamPreferences.displayName?.let {
            if(it.isNotEmpty()) {
                return true
            }
        }

        return false
    }

}