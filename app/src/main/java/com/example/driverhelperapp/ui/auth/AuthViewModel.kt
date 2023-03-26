package com.example.driverhelperapp.ui.auth

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.text.InputFilter
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.driverhelperapp.BaseViewModel
import com.example.driverhelperapp.CoroutinesErrorHandler
import com.example.driverhelperapp.models.JwtRequest
import com.example.driverhelperapp.models.JwtResponse
import com.example.driverhelperapp.models.SignUpDto
import com.example.driverhelperapp.repositories.AuthRepository
import com.example.driverhelperapp.utils.ApiResponse
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.ResponseBody
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
): BaseViewModel() {

    private val _loginResponse = MutableLiveData<ApiResponse<JwtResponse>>()
    val loginResponse = _loginResponse

    private val _signupResponse = MutableLiveData<ApiResponse<ResponseBody>>()
    val signupResponse = _signupResponse

    fun login(authRequest: JwtRequest, coroutinesErrorHandler: CoroutinesErrorHandler) = baseRequest(
        _loginResponse,
        coroutinesErrorHandler
    ) {
        authRepository.login(authRequest)
    }

    fun signup(signUpDto: SignUpDto, coroutinesErrorHandler: CoroutinesErrorHandler) = baseRequest(
        _signupResponse,
        coroutinesErrorHandler
    ) {
        authRepository.registerUser(signUpDto)
    }

    fun setupUsernameInputFilter(editText: TextInputEditText) {
        editText.filters = arrayOf(
            InputFilter.LengthFilter(15),
            InputFilter { source, start, end, _, _, _ ->
            for (i in start until end) {
                if (!source[i].isLetterOrDigit()) {
                    return@InputFilter ""
                }
            }
            null
        })
    }

    fun setupPasswordInputFilter(editText: TextInputEditText) {
        editText.filters = arrayOf(
            InputFilter.LengthFilter(30),
            InputFilter { source, start, end, _, _, _ ->
            for (i in start until end) {
                if (Character.isWhitespace(source[i])) {
                    return@InputFilter ""
                }
            }
            null
        })
    }
}