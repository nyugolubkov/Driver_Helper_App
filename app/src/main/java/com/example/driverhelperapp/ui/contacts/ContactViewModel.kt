package com.example.driverhelperapp.ui.contacts

import android.text.InputFilter
import androidx.lifecycle.MutableLiveData
import com.example.driverhelperapp.BaseViewModel
import com.example.driverhelperapp.CoroutinesErrorHandler
import com.example.driverhelperapp.models.Contact
import com.example.driverhelperapp.repositories.ContactRepository
import com.example.driverhelperapp.utils.ApiResponse
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.ResponseBody
import javax.inject.Inject

@HiltViewModel
class ContactViewModel @Inject constructor(
    private val contactRepository: ContactRepository,
): BaseViewModel() {

    private val _contacts = MutableLiveData<ApiResponse<List<Contact>>>()
    val contacts = _contacts

    private val _contactAdd = MutableLiveData<ApiResponse<Contact>>()
    val contactAdd = _contactAdd

    private val _contactChange = MutableLiveData<ApiResponse<Contact>>()
    val contactChange = _contactChange
    private var changingContact: Contact? = null

    private val _contactDelete = MutableLiveData<ApiResponse<ResponseBody>>()
    val contactDelete = _contactDelete

    fun getContacts(coroutinesErrorHandler: CoroutinesErrorHandler)  = baseRequest(
        _contacts,
        coroutinesErrorHandler
    ) {
        contactRepository.getContacts()
    }

    fun addNewContact(contact: Contact, coroutinesErrorHandler: CoroutinesErrorHandler)  = baseRequest(
        _contactAdd,
        coroutinesErrorHandler
    ) {
        contactRepository.addNewContact(contact)
    }

    fun putContact(contact: Contact, contactId: Long, coroutinesErrorHandler: CoroutinesErrorHandler)  = baseRequest(
        _contactChange,
        coroutinesErrorHandler
    ) {
        contactRepository.putContact(contact, contactId)
    }

    fun deleteContact(contactId: Long, coroutinesErrorHandler: CoroutinesErrorHandler)  = baseRequest(
        _contactDelete,
        coroutinesErrorHandler
    ) {
        contactRepository.deleteContact(contactId)
    }

    fun setChangingContact(id: Long?, name: String, number: String) {
        changingContact = Contact(id, name, number)
    }

    fun getChangingContactName(): String {
        return changingContact?.name ?: ""
    }

    fun getChangingContactPhone(): String {
        return changingContact?.number ?: ""
    }

    fun getChangingContactId(): Long? {
        return changingContact?.id
    }

    fun setupNameInputFilter(editText: TextInputEditText) {
        editText.filters = arrayOf(InputFilter.LengthFilter(15))
    }

    fun setupPhoneInputFilter(editText: TextInputEditText) {
        editText.filters = arrayOf(
            InputFilter.LengthFilter(16),
            InputFilter { source, start, end, _, _, _ ->
                for (i in start until end) {
                    if (source[i].isLetter()) {
                        return@InputFilter ""
                    }
                }
                null
            })
    }
}