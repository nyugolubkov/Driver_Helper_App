package com.example.driverhelperapp.utils

import com.example.driverhelperapp.services.AuthApiService
import com.example.driverhelperapp.models.JwtResponse
import com.example.driverhelperapp.models.RefreshJwtRequest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject

class AuthAuthenticator @Inject constructor(
    private val tokenManager: TokenManager
): Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        val accessToken = runBlocking {
            tokenManager.getAccessToken().first()
        }
        return runBlocking {
            val newToken = getNewToken(accessToken)

            if (!newToken.isSuccessful || newToken.body() == null) {
                tokenManager.deleteAccessToken()
                tokenManager.deleteRefreshToken()
            }

            newToken.body()?.let {
                it.accessToken?.let { accessToken ->
                    tokenManager.saveAccessToken(accessToken)
                    response.request.newBuilder()
                        .header("Authorization", "Bearer $accessToken")
                        .build()
                }
            }
        }
    }

    private suspend fun getNewToken(refreshToken: String?): retrofit2.Response<JwtResponse> {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        val okHttpClient = OkHttpClient.Builder().addInterceptor(loggingInterceptor).build()

        val retrofit = Retrofit.Builder()
            .baseUrl("http://51.250.64.142:8080")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
        val authApiService = retrofit.create(AuthApiService::class.java)
        val nonNullRefreshToken = refreshToken ?: ""
        return authApiService.getNewAccessToken(RefreshJwtRequest(nonNullRefreshToken))
    }


}