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
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.example.driverhelperapp.CoroutinesErrorHandler
import com.example.driverhelperapp.R
import com.example.driverhelperapp.models.JwtRequest
import com.example.driverhelperapp.utils.ApiResponse
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LogInFragment : Fragment() {

    private val viewModel: AuthViewModel by activityViewModels()
    private val tokenViewModel: TokenViewModel by activityViewModels()

    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_log_in, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = view.findNavController()

        val progressBar = view.findViewById<ProgressBar>(R.id.loginProgressBar)
        val overlay = view.findViewById<View>(R.id.loginOverlay)
        val errorTextView = view.findViewById<TextView>(R.id.errorLoginTextView)
        val usernameEditText = view.findViewById<TextInputEditText>(R.id.usernameLoginEditText)
        val passwordEditText = view.findViewById<TextInputEditText>(R.id.passwordLoginEditText)

        tokenViewModel.accessToken.observe(viewLifecycleOwner) { token ->
            if (token != null)
                navController.navigate(LogInFragmentDirections.actionLogInFragmentToMainNavGraph())
        }

        tokenViewModel.refreshToken.observe(viewLifecycleOwner) { token ->
            if (token != null)
                tokenViewModel.saveRefreshToken(token)
        }

        viewModel.loginResponse.observe(viewLifecycleOwner) {
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
                    it.data.accessToken?.let { it1 -> tokenViewModel.saveAccessToken(it1) }
                    it.data.refreshToken?.let { it1 -> tokenViewModel.saveRefreshToken(it1) }
                }
                else -> {}
            }
        }

        viewModel.setupUsernameInputFilter(usernameEditText)
        viewModel.setupPasswordInputFilter(passwordEditText)

        setLoginLength(view)

        view.findViewById<TextView>(R.id.toRegisterTextView).setOnClickListener {
            navController.navigate(LogInFragmentDirections.actionLogInFragmentToSignUpFragment())
        }

        view.findViewById<Button>(R.id.submitLoginButton).setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()
            viewModel.login(
                JwtRequest(username, password),
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
        val loginInputLayout = view.findViewById<TextInputLayout>(R.id.usernameLoginInputLayout)
        val passwordInputLayout = view.findViewById<TextInputLayout>(R.id.passwordLoginInputLayout)
        val submitButton = view.findViewById<Button>(R.id.submitLoginButton)
        submitButton.isEnabled = false

        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val loginLength = loginInputLayout.editText?.text?.length ?: 0
                val passwordLength = passwordInputLayout.editText?.text?.length ?: 0

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

                submitButton.isEnabled = loginLength >= 5 && passwordLength >= 8
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        loginInputLayout.editText?.addTextChangedListener(textWatcher)
        passwordInputLayout.editText?.addTextChangedListener(textWatcher)
    }

}