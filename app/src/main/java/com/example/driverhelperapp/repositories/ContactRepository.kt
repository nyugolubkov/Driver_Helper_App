package com.example.driverhelperapp.repositories

import com.example.driverhelperapp.services.ContactApiService
import com.example.driverhelperapp.models.Contact
import com.example.driverhelperapp.utils.apiRequestFlow
import javax.inject.Inject

class ContactRepository @Inject constructor(
    private val contactApiService: ContactApiService,
) {
    fun getContacts() = apiRequestFlow {
        contactApiService.getContacts()
    }

    fun addNewContact(contact: Contact) = apiRequestFlow {
        contactApiService.addNewContact(contact)
    }

    fun putContact(contact: Contact, contactId: Long) = apiRequestFlow {
        contactApiService.putContact(contact, contactId)
    }

    fun deleteContact(contactId: Long) = apiRequestFlow {
        contactApiService.deleteContact(contactId)
    }
}