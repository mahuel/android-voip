package com.example.videostream.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.example.videostream.perferences.IVideoStreamPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LaunchViewModel @Inject constructor(
    private val videoStreamPreferences: IVideoStreamPreferences
) : ViewModel() {

    fun isDisplayNameSet(): Boolean {
        videoStreamPreferences.getDisplayName()?.let {
            if(it.isNotEmpty()) {
                return true
            }
        }

        return false
    }

}