package com.example.driverhelperapp.utils

import android.util.Log
import com.example.driverhelperapp.models.RefreshJwtRequest
import com.example.driverhelperapp.services.AuthApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager
): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        Log.d("INTERCEPTOR", "Here we are")
        val accessToken = runBlocking {
            tokenManager.getAccessToken().first()
        }
        val request = chain.request()
        val newRequest = request.newBuilder()
            .header("Authorization", "Bearer $accessToken")
        return chain.proceed(newRequest.build())
    }

}