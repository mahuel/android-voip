package com.example.videostream.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.example.videostream.perferences.IVideoStreamPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EnterNameViewModel @Inject constructor(
    private val videoStreamPreferences: IVideoStreamPreferences
) : ViewModel() {


    fun nameEntered(displayName: String) : Boolean{
        if (displayName.trim().isNotEmpty()) {
            videoStreamPreferences.setDisplayName(displayName)
            return true
        }
        return false
    }
}