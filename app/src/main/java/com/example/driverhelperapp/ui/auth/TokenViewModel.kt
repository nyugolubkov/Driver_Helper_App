package com.example.driverhelperapp.ui.auth

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.driverhelperapp.utils.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class TokenViewModel @Inject constructor(
    private val tokenManager: TokenManager,
): ViewModel() {

    val accessToken = MutableLiveData<String?>()
    val refreshToken = MutableLiveData<String?>()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            tokenManager.getAccessToken().collect {
                withContext(Dispatchers.Main) {
                    accessToken.value = it
                }

            }
            tokenManager.getRefreshToken().collect {
                withContext(Dispatchers.Main) {
                    refreshToken.value = it
                }
            }
        }
    }

    fun saveAccessToken(accessToken: String) {
        viewModelScope.launch(Dispatchers.IO) {
            tokenManager.saveAccessToken(accessToken)
        }
    }

    fun saveRefreshToken(refreshToken: String) {
        viewModelScope.launch(Dispatchers.IO) {
            tokenManager.saveRefreshToken(refreshToken)
        }
    }

    fun deleteAccessToken() {
        viewModelScope.launch(Dispatchers.IO) {
            tokenManager.deleteAccessToken()
        }
    }

    fun deleteRefreshToken() {
        viewModelScope.launch(Dispatchers.IO) {
            tokenManager.deleteRefreshToken()
        }
    }

    fun isInternetConnected(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val networkCapabilities =
            connectivityManager.getNetworkCapabilities(network) ?: return false
        return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}