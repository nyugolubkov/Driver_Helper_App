package com.example.driverhelperapp.services

import com.example.driverhelperapp.models.JwtRequest
import com.example.driverhelperapp.models.JwtResponse
import com.example.driverhelperapp.models.RefreshJwtRequest
import com.example.driverhelperapp.models.SignUpDto
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthApiService {
    @POST("/auth/refresh")
    suspend fun getNewRefreshToken(
        @Body request: RefreshJwtRequest,
    ): Response<JwtResponse>

    @POST("/auth/signin")
    suspend fun login(
        @Body authRequest: JwtRequest,
    ): Response<JwtResponse>

    @POST("/auth/signup")
    suspend fun registerUser(
        @Body signUpDto: SignUpDto,
    ): Response<ResponseBody>

    @POST("/auth/token")
    suspend fun getNewAccessToken(
        @Body request: RefreshJwtRequest,
    ): Response<JwtResponse>
}