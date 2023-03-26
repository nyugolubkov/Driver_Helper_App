package com.example.driverhelperapp.services

import com.example.driverhelperapp.models.Ride
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST

interface RideApiService {
    @GET("/ride/current")
    suspend fun getCurrentRide(): Response<Ride>

    @POST("/ride/start")
    suspend fun startRide(): Response<String>

    @POST("/ride/finish")
    suspend fun finishRide(): Response<Ride>
}