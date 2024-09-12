package com.example.androidcourselevel5.data.retrofit.model

import java.io.File
import java.io.Serializable
import java.util.Date

data class Contact(
    val id: Int,
    val name: String?,
    val career: String?,
    val address: String?,
    val image: File? = null
) : Serializable

data class ContactInfo(
    val id: Int,
    val email: String,
    val name: String?,
    val phone: String?,
    val address: String?,
    val career: String?,
    val birthday: Date?,
    val facebook: String?,
    val instagram: String?,
    val twitter: String?,
    val linkedin: String?,
    val image: File?
)

data class AllContacts(
    val contacts: List<Contact>
)

data class ContactId(
    val contactId: Int
)