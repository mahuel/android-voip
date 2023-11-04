package com.example.videostream.presentation.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.videostream.R
import com.example.videostream.databinding.ActivityCallBinding
import com.example.videostream.domain.model.CallState
import com.example.videostream.domain.model.Contact
import com.example.videostream.presentation.viewmodel.CallViewModel
import com.example.videostream.utils.makeToast
import dagger.hilt.android.AndroidEntryPoint
import java.net.InetAddress

@AndroidEntryPoint
class CallActivity : AppCompatActivity() {
    companion object {
        const val NAME_EXTRA = "NAME_EXTRA"
        const val ADDRESS_EXTRA = "ADDRESS_EXTRA"
        const val ANSWER_EXTRA = "ANSWER_EXTRA"
        fun start(activity: AppCompatActivity, contact: Contact, answerCall: Boolean) {
            val intent = Intent(
                activity,
                CallActivity::class.java
            )

            Bundle().apply {
                putString(NAME_EXTRA, contact.name)
                putString(ADDRESS_EXTRA, contact.address.hostAddress)
                putBoolean(ANSWER_EXTRA, answerCall)
                intent.putExtras(this)
            }

            activity.startActivity(intent)
        }
    }

    private val callViewModel: CallViewModel by viewModels()

    private val name: String by lazy {
        intent?.extras?.getString(NAME_EXTRA, "") ?: ""
    }

    private val address: String by lazy {
        intent?.extras?.getString(ADDRESS_EXTRA, "") ?: ""
    }

    private val answer: Boolean by lazy {
        intent?.extras?.getBoolean(ANSWER_EXTRA) ?: false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: ActivityCallBinding = ActivityCallBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d("TEST123", "name:$name")
        Log.d("TEST123", "address:$address")
        Log.d("TEST123", "answer:$answer")

        callViewModel.getCallStatus().observe(this) {
            when (it.status) {
                CallState.STARTED -> {
                    binding.callStatusTextView.text = name
                }

                CallState.STOPPED -> {
                    binding.callStatusTextView.text = ""
                    finish()
                }

                CallState.INCOMING -> {
                    binding.callStatusTextView.text = getString(R.string.status_incoming, name)
                }

                CallState.OUTGOING -> {
                    binding.callStatusTextView.text = getString(R.string.status_outgoing, name)
                }
            }
        }

        if (answer) {
            callViewModel.connectCall(
                InetAddress.getByName(address)
            )
        } else {
            callViewModel.outgoingCall(
                InetAddress.getByName(address)
            )
        }

        binding.muteMicBtn.setOnClickListener {
            callViewModel.microphoneToggle(requestPermissionLauncher)
        }

        callViewModel.getMicrophoneEnable().observe(this) {
            binding.muteMicBtn.apply {
                if (it) {
                    setImageResource(R.drawable.ic_mic_on)
                } else {
                    setImageResource(R.drawable.ic_mic_off)
                }
            }
        }

        binding.callEndBtn.setOnClickListener {
            callViewModel.stopCall()
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (!isGranted) {
                this.makeToast(
                    "Microphone permission not granted"
                )
            }
        }
}