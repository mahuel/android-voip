package com.example.videostream.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.videostream.R
import com.example.videostream.domain.model.Contact

class ContactListAdapter(
    private val contactListListener: ContactListAdapterListener
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var contacts: MutableList<Contact> = mutableListOf()
    fun addContacts(contacts: List<Contact>) {
        this.contacts = contacts.toMutableList()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ContactViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_contact, parent, false),
            contactListListener
        )
    }

    override fun getItemCount(): Int {
        return contacts.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ContactViewHolder).bind(contacts[position])
    }

    inner class ContactViewHolder(
        itemView: View,
        private val contactListListener: ContactListAdapterListener
    ) : RecyclerView.ViewHolder(itemView) {
        fun bind(contact: Contact) {
            itemView.findViewById<TextView>(R.id.contactNameTextView).text = contact.name
            itemView.findViewById<ImageButton>(R.id.contactCallBtn).setOnClickListener {
                contactListListener.onCallPressed(contact)
            }
        }
    }
}