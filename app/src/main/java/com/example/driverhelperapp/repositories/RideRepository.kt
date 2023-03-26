package com.example.driverhelperapp.repositories

import com.example.driverhelperapp.services.RideApiService
import com.example.driverhelperapp.utils.apiRequestFlow
import javax.inject.Inject

class RideRepository @Inject constructor(
    private val rideApiService: RideApiService,
) {
    fun getCurrentRide() = apiRequestFlow {
        rideApiService.getCurrentRide()
    }

    fun startRide() = apiRequestFlow {
        rideApiService.startRide()
    }

    fun finishRide() = apiRequestFlow {
        rideApiService.finishRide()
    }
}