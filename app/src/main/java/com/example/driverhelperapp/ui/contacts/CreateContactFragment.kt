package com.example.driverhelperapp.ui.contacts

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
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

class CreateContactFragment : Fragment() {

    private val contactViewModel: ContactViewModel by activityViewModels()

    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_contact, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = view.findNavController()

        val progressBar = view.findViewById<ProgressBar>(R.id.contactAddProgressBar)
        val overlay = view.findViewById<View>(R.id.contactAddOverlay)
        val errorTextView = view.findViewById<TextView>(R.id.errorContactAddTextView)
        val nameEditText = view.findViewById<TextInputEditText>(R.id.nameContactAddEditText)
        val phoneEditText = view.findViewById<TextInputEditText>(R.id.phoneContactAddEditText)
        val submitButton = view.findViewById<Button>(R.id.submitContactAddButton)
        submitButton.isEnabled =
            (nameEditText.text?.length ?: 0) > 0 && (phoneEditText.text?.length ?: 0) == 16

        phoneEditText.addTextChangedListener(MaskWatcher("8(###) ###-##-##"))

        view.findViewById<Button>(R.id.submitContactAddButton).setOnClickListener {

            contactViewModel.contactAdd.observe(viewLifecycleOwner) {
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
            contactViewModel.addNewContact(
                Contact(null, name, phone),
                object: CoroutinesErrorHandler {
                    @SuppressLint("SetTextI18n")
                    override fun onError(message: String) {
                        errorTextView.text = "Ошибка! $message"
                    }
                }
            )
        }

        view.findViewById<ImageButton>(R.id.backContactAddButton).setOnClickListener {
            navController.navigate(MainFragmentDirections.actionGlobalMainFragment())
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