package com.example.videostream.presentation.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.videostream.databinding.ActivityContactListBinding
import com.example.videostream.domain.model.Contact
import com.example.videostream.presentation.adapter.ContactListAdapter
import com.example.videostream.presentation.adapter.ContactListAdapterListener
import com.example.videostream.presentation.viewmodel.CallViewModel
import com.example.videostream.presentation.viewmodel.ContactListViewModel
import com.example.videostream.utils.makeToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ContactListActivity : AppCompatActivity() {

    private val contactListViewModel: ContactListViewModel by viewModels()

    private val callViewModel: CallViewModel by viewModels()

    private val contactListAdapter = ContactListAdapter(
        getContactListListener()
    )

    companion object {
        const val REJECT_EXTRA = "REJECT_EXTRA"
    }

    private val reject: Boolean by lazy {
        intent?.extras?.getBoolean(REJECT_EXTRA) ?: false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: ActivityContactListBinding = ActivityContactListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.myToolbar)


        binding.signOutBtn.setOnClickListener {
            contactListViewModel.signOut()
            contactListViewModel.stopMessagingService()
            finish()
            this.startActivity(
                Intent(
                    this,
                    LaunchActivity::class.java
                )
            )
        }

        binding.addContactBtn.setOnClickListener {
            contactListViewModel.getAddress()?.also {
                val fragment = AddContactBottomSheetFragment()
                if (fragment.isAdded) {
                    return@setOnClickListener
                }
                fragment.show(supportFragmentManager, "AddContact")
            } ?: run {
                this.makeToast(
                    "Looks like your address is NOT Ipv4"
                )
            }
        }

        val displayName = contactListViewModel.getDisplayName()

        displayName?.let {
            supportActionBar?.title = "Hi $it"
        }

        binding.contactListRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@ContactListActivity)
            adapter = contactListAdapter
        }

        contactListViewModel.getContacts().observe(this) {
            contactListAdapter.addContacts(it)
        }

        callViewModel.getIncomingCalls().observe(this) {

        }

        if (reject) {
            callViewModel.stopCall()
        }
    }

    override fun onResume() {
        super.onResume()
        contactListViewModel.startMessagingService()
    }

    private fun getContactListListener() = object : ContactListAdapterListener {
        override fun onCallPressed(contact: Contact) {
            CallActivity.start(
                activity = this@ContactListActivity,
                contact = contact,
                answerCall = false
            )
        }
    }
}