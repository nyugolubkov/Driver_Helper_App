package com.example.driverhelperapp.services

import com.example.driverhelperapp.models.WeatherEntity
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    @GET("/weather")
    suspend fun getWeather(
        @Query("fake") fake: Boolean,
        @Query("lat") lat: String,
        @Query("lon") lon: String
    ): Response<WeatherEntity>
}