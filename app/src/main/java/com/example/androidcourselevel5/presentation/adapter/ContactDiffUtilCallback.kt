package com.example.androidcourselevel5.presentation.adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.androidcourselevel5.data.retrofit.model.Contact

class ContactDiffUtilCallback : DiffUtil.ItemCallback<Contact>() {
    override fun areItemsTheSame(oldItem: Contact, newItem: Contact): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Contact, newItem: Contact): Boolean {
        return oldItem == newItem
    }
}
