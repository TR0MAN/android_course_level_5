package com.example.androidcourselevel5.presentation.adapter

import com.example.androidcourselevel5.data.retrofit.model.Contact

interface ClickListener

interface ElementClickListener: ClickListener {
    fun onElementClickAction(contact: Contact)
}

interface ExtendedElementClickListener : ElementClickListener {
    fun onElementProfileClick(contact: Contact)

    fun onElementLongClick(contactId: Int)

    fun onElementChecked(checkBoxState: Boolean, contactId: Int)
}
