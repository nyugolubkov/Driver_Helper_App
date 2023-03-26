package com.example.driverhelperapp.ui.auth

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
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.example.driverhelperapp.CoroutinesErrorHandler
import com.example.driverhelperapp.R
import com.example.driverhelperapp.models.SignUpDto
import com.example.driverhelperapp.utils.ApiResponse
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class SignUpFragment : Fragment() {

    companion object {
        fun newInstance() = SignUpFragment()
    }

    private val viewModel: AuthViewModel by activityViewModels()

    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sign_up, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = view.findNavController()

        val progressBar = view.findViewById<ProgressBar>(R.id.signupProgressBar)
        val overlay = view.findViewById<View>(R.id.signupOverlay)
        val errorTextView = view.findViewById<TextView>(R.id.errorSignupTextView)
        val usernameEditText = view.findViewById<TextInputEditText>(R.id.usernameSignupEditText)
        val passwordEditText = view.findViewById<TextInputEditText>(R.id.passwordSignupEditText)
        val confPasswordEditText = view.findViewById<TextInputEditText>(R.id.confPasswordSignupEditText)

        viewModel.signupResponse.observe(viewLifecycleOwner) {
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
                    navController.navigate(SignUpFragmentDirections.actionSignUpFragmentToLogInFragment())
                }
            }
        }

        viewModel.setupUsernameInputFilter(usernameEditText)
        viewModel.setupPasswordInputFilter(passwordEditText)
        viewModel.setupPasswordInputFilter(confPasswordEditText)

        setLoginLength(view)

        view.findViewById<TextView>(R.id.toLoginTextView).setOnClickListener {
            navController.navigate(SignUpFragmentDirections.actionSignUpFragmentToLogInFragment())
        }

        view.findViewById<Button>(R.id.submitSignupButton).setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()
            viewModel.signup(
                SignUpDto(username, "", password),
                object: CoroutinesErrorHandler {
                    @SuppressLint("SetTextI18n")
                    override fun onError(message: String) {
                        errorTextView.text = "Ошибка! $message"
                    }
                }
            )
        }
    }

    private fun setLoginLength(view: View) {
        val loginInputLayout = view.findViewById<TextInputLayout>(R.id.usernameSignupInputLayout)
        val passwordInputLayout = view.findViewById<TextInputLayout>(R.id.passwordSignupInputLayout)
        val confPasswordInputLayout = view.findViewById<TextInputLayout>(R.id.confPasswordSignupInputLayout)
        val submitButton = view.findViewById<Button>(R.id.submitSignupButton)
        submitButton.isEnabled = false

        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val loginLength = loginInputLayout.editText?.text?.length ?: 0
                val passwordLength = passwordInputLayout.editText?.text?.length ?: 0
                val confPasswordLength = confPasswordInputLayout.editText?.text?.length ?: 0

                val passwordText = passwordInputLayout.editText?.text ?: ""
                val confPasswordText = confPasswordInputLayout.editText?.text ?: ""

                if (loginLength < 5) {
                    loginInputLayout.error = "Логин должен быть не менее 5 символов"
                } else {
                    loginInputLayout.error = null
                }

                if (passwordLength < 8) {
                    passwordInputLayout.error = "Пароль должен быть не менее 8 символов"
                } else {
                    passwordInputLayout.error = null
                }

                if (confPasswordLength < 8) {
                    confPasswordInputLayout.error = "Пароль должен быть не менее 8 символов"
                } else {
                    confPasswordInputLayout.error = null
                }

                submitButton.isEnabled = loginLength >= 5 && passwordLength >= 8
                        && passwordText.toString() == confPasswordText.toString()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        loginInputLayout.editText?.addTextChangedListener(textWatcher)
        passwordInputLayout.editText?.addTextChangedListener(textWatcher)
        confPasswordInputLayout.editText?.addTextChangedListener(textWatcher)
    }

}