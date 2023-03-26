package com.example.driverhelperapp.repositories

import com.example.driverhelperapp.services.WeatherApiService
import com.example.driverhelperapp.utils.apiRequestFlow
import javax.inject.Inject

class WeatherRepository @Inject constructor(
    private val weatherApiService: WeatherApiService,
){
    fun getWeather(fake: Boolean, lat: String, lon: String) = apiRequestFlow {
        weatherApiService.getWeather(fake, lat, lon)
    }
}