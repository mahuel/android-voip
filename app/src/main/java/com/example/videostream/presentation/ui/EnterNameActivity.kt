package com.example.videostream.presentation.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.videostream.databinding.AcitivtyEnterNameBinding
import com.example.videostream.presentation.viewmodel.EnterNameViewModel
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

    }
}