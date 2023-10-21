package com.example.videostream.presentation.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.videostream.presentation.viewmodel.LaunchViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LaunchActivity : AppCompatActivity() {
    private val launchViewModel: LaunchViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (launchViewModel.isDisplayNameSet()) {
            this.startActivity(
                Intent(
                    this,
                    ContactListActivity::class.java
                )
            )
        } else {
            this.startActivity(
                Intent(
                    this,
                    EnterNameActivity::class.java
                )
            )
        }
    }
}