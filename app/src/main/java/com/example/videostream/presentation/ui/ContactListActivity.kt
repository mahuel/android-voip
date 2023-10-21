package com.example.videostream.presentation.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.videostream.databinding.ActivityContactListBinding
import com.example.videostream.domain.model.Contact
import com.example.videostream.presentation.adapter.ContactListAdapter
import com.example.videostream.presentation.viewmodel.ContactListViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ContactListActivity : AppCompatActivity(){

    private val contactListViewModel : ContactListViewModel by viewModels()

    private val contactListAdapter = ContactListAdapter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: ActivityContactListBinding = ActivityContactListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.signOutBtn.setOnClickListener {
            contactListViewModel.signOut()

            this.startActivity(
                Intent(
                    this,
                    LaunchActivity::class.java
                )
            )
        }

        val displayName = contactListViewModel.getDisplayName()

        displayName?.let {
            binding.headingTextView.text = "Hi $it"
        }

        binding.contactListRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@ContactListActivity)
            adapter = contactListAdapter
        }

        contactListAdapter.addContacts(
            arrayListOf<Contact>().apply {
                add(Contact("allen"))
                add(Contact("allen"))
                add(Contact("allen"))
                add(Contact("allen"))
                add(Contact("allen"))
                add(Contact("allen"))
                add(Contact("allen"))
                add(Contact("allen"))
                add(Contact("allen"))
            }
        )
    }

    override fun onResume() {
        super.onResume()
        contactListViewModel.startAddressBroadcastService()
    }

    override fun onPause() {
        super.onPause()
        contactListViewModel.stopAddressBroadcastService()
    }
}