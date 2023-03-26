package com.example.driverhelperapp.ui.contacts

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.example.driverhelperapp.CoroutinesErrorHandler
import com.example.driverhelperapp.R
import com.example.driverhelperapp.models.Contact
import com.example.driverhelperapp.ui.main.MainFragmentDirections
import com.example.driverhelperapp.utils.ApiResponse
import com.example.driverhelperapp.utils.MaskWatcher
import com.google.android.material.textfield.TextInputEditText

class ChangeContactFragment : Fragment() {

    private val contactViewModel: ContactViewModel by activityViewModels()

    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_change_contact, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = view.findNavController()

        val progressBar = view.findViewById<ProgressBar>(R.id.contactChangeProgressBar)
        val overlay = view.findViewById<View>(R.id.contactChangeOverlay)
        val errorTextView = view.findViewById<TextView>(R.id.errorContactChangeTextView)
        val nameEditText = view.findViewById<TextInputEditText>(R.id.nameContactChangeEditText)
        val phoneEditText = view.findViewById<TextInputEditText>(R.id.phoneContactChangeEditText)
        val submitButton = view.findViewById<Button>(R.id.submitContactChangeButton)
        submitButton.isEnabled =
            (nameEditText.text?.length ?: 0) > 0 && (phoneEditText.text?.length ?: 0) == 16

        nameEditText.setText(contactViewModel.getChangingContactName())
        phoneEditText.setText(contactViewModel.getChangingContactPhone())

        phoneEditText.addTextChangedListener(MaskWatcher("8(###) ###-##-##"))

        view.findViewById<Button>(R.id.submitContactChangeButton).setOnClickListener {

            contactViewModel.contactChange.observe(viewLifecycleOwner) {
                when(it) {
                    is ApiResponse.Failure -> {
                        progressBar.visibility = View.GONE
                        overlay.visibility = View.GONE
                        errorTextView.text = it.errorMessage
                        Log.d("ERROR", it.errorMessage)
                    }
                    is ApiResponse.Loading -> {
                        progressBar.visibility = View.VISIBLE
                        overlay.visibility = View.VISIBLE
                        errorTextView.text = ""
                    }
                    is ApiResponse.Success -> {
                        progressBar.visibility = View.GONE
                        overlay.visibility = View.GONE
                        errorTextView.text = ""
                        navController.navigate(MainFragmentDirections.actionGlobalMainFragment())
                    }
                    else -> {}
                }
            }
            val name = nameEditText.text.toString()
            val phone = phoneEditText.text.toString()
            val id = contactViewModel.getChangingContactId()
            if (id != null) {
                contactViewModel.putContact(
                    Contact(null, name, phone), id,
                    object: CoroutinesErrorHandler {
                        @SuppressLint("SetTextI18n")
                        override fun onError(message: String) {
                            errorTextView.text = "Ошибка! $message"
                        }
                    }
                )
            }
            else {
                Toast.makeText(this.context, "Непредвиденная ошибка!", Toast.LENGTH_LONG).show()
                navController.navigate(MainFragmentDirections.actionGlobalMainFragment())
            }
        }

        view.findViewById<ImageButton>(R.id.backContactChangeButton).setOnClickListener {
            navController.navigate(MainFragmentDirections.actionGlobalMainFragment())
        }

        view.findViewById<ImageButton>(R.id.deleteContactChangeButton).setOnClickListener {
            contactViewModel.contactDelete.observe(viewLifecycleOwner) {
                when(it) {
                    is ApiResponse.Failure -> {
                        progressBar.visibility = View.GONE
                        overlay.visibility = View.GONE
                        errorTextView.text = it.errorMessage
                        Log.d("ERROR", it.errorMessage)
                    }
                    is ApiResponse.Loading -> {
                        progressBar.visibility = View.VISIBLE
                        overlay.visibility = View.VISIBLE
                        errorTextView.text = ""
                    }
                    is ApiResponse.Success -> {
                        progressBar.visibility = View.GONE
                        overlay.visibility = View.GONE
                        errorTextView.text = ""
                        navController.navigate(MainFragmentDirections.actionGlobalMainFragment())
                    }
                    else -> {}
                }
            }
            val alertDialog = AlertDialog.Builder(this.context).create()
            alertDialog.setTitle("Подтверждение")
            alertDialog.setMessage("Вы уверены, что хотите удалить этот контакт?")
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Да") { _, _ ->
                val contactId = contactViewModel.getChangingContactId()
                if (contactId != null) {
                    contactViewModel.deleteContact(
                        contactId,
                        object : CoroutinesErrorHandler {
                            @SuppressLint("SetTextI18n")
                            override fun onError(message: String) {
                                Toast.makeText(context, "Ошибка! $message", Toast.LENGTH_LONG).show()
                            }
                        }
                    )
                }
                else {
                    Toast.makeText(this.context, "С текущим контактом произошла ошибка!", Toast.LENGTH_LONG).show()
                }
            }
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Нет") { _, _ -> }
            alertDialog.show()
        }

        contactViewModel.setupNameInputFilter(nameEditText)
        contactViewModel.setupPhoneInputFilter(phoneEditText)

        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val nameLength = nameEditText.text?.length ?: 0
                val phoneLength = phoneEditText.text?.length ?: 0

                submitButton.isEnabled = nameLength > 0 && phoneLength == 16
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        nameEditText.addTextChangedListener(textWatcher)
        phoneEditText.addTextChangedListener(textWatcher)
    }
}