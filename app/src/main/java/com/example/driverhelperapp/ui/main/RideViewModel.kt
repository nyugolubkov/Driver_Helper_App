package com.example.driverhelperapp.ui.main

import android.os.Handler
import androidx.lifecycle.MutableLiveData
import com.example.driverhelperapp.BaseViewModel
import com.example.driverhelperapp.CoroutinesErrorHandler
import com.example.driverhelperapp.models.Ride
import com.example.driverhelperapp.models.WeatherEntity
import com.example.driverhelperapp.repositories.RideRepository
import com.example.driverhelperapp.repositories.WeatherRepository
import com.example.driverhelperapp.utils.ApiResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RideViewModel @Inject constructor(
    private val rideRepository: RideRepository,
): BaseViewModel() {
    private var startTime: Long = 0L
    private var elapsedTime: Long = 0L
    private var status: CarRideStatus = CarRideStatus.BASE

    private val _start = MutableLiveData<ApiResponse<String>>()
    val start = _start

    private val _finish = MutableLiveData<ApiResponse<Ride>>()
    val finish = _finish

    fun startRide(
        coroutinesErrorHandler: CoroutinesErrorHandler
    )  = baseRequest(
        _start,
        coroutinesErrorHandler
    ) {
        startTime = System.currentTimeMillis() - elapsedTime
        status = CarRideStatus.STARTED
        rideRepository.startRide()
    }

    fun finishRide(
        coroutinesErrorHandler: CoroutinesErrorHandler)  = baseRequest(
        _finish,
        coroutinesErrorHandler
    ) {
        elapsedTime = System.currentTimeMillis() - startTime
        status = CarRideStatus.FINISHED
        rideRepository.finishRide()
    }

    fun resetRide() {
        startTime = System.currentTimeMillis()
        elapsedTime = 0L
        status = CarRideStatus.BASE
    }

    fun getElapsedTime(): Long {
        elapsedTime = System.currentTimeMillis() - startTime
        return elapsedTime
    }

    fun rideStatus(): CarRideStatus{
        return status
    }
}