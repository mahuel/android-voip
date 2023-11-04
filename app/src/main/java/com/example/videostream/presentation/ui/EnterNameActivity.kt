package com.example.videostream.presentation.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.videostream.databinding.AcitivtyEnterNameBinding
import com.example.videostream.presentation.viewmodel.EnterNameViewModel
import com.example.videostream.utils.makeToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EnterNameActivity : AppCompatActivity() {

    private val enterNameViewModel: EnterNameViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: AcitivtyEnterNameBinding = AcitivtyEnterNameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.enterNameBtn.setOnClickListener {
            val result = enterNameViewModel.nameEntered(
                displayName = binding.enterNameEditText.text.toString()
            )

            if (result) {
                finish()
                this.startActivity(
                    Intent(
                        this,
                        ContactListActivity::class.java
                    )
                )
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when (PackageManager.PERMISSION_GRANTED) {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) -> {
                    // Do nothing
                }

                else -> {
                    registerForActivityResult(
                        ActivityResultContracts.RequestPermission()){
                        if (!it) {
                            applicationContext.makeToast(
                                "Notification permissions are needed to show incoming calls"
                            )
                        }
                    }.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }

    }
}