package com.example.driverhelperapp.repositories

import com.example.driverhelperapp.services.AuthApiService
import com.example.driverhelperapp.models.JwtRequest
import com.example.driverhelperapp.models.RefreshJwtRequest
import com.example.driverhelperapp.models.SignUpDto
import com.example.driverhelperapp.utils.apiRequestFlow
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val authApiService: AuthApiService,
) {
    fun getNewRefreshToken(request: RefreshJwtRequest) = apiRequestFlow {
        authApiService.getNewRefreshToken(request)
    }

    fun login(authRequest: JwtRequest) = apiRequestFlow {
        authApiService.login(authRequest)
    }

    fun registerUser(signUpDto: SignUpDto) = apiRequestFlow {
        authApiService.registerUser(signUpDto)
    }

    fun getNewAccessToken(request: RefreshJwtRequest) = apiRequestFlow {
        authApiService.getNewAccessToken(request)
    }
}