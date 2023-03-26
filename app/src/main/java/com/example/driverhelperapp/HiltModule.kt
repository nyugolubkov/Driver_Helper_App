package com.example.driverhelperapp

import com.example.driverhelperapp.repositories.*
import com.example.driverhelperapp.services.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
class HiltModule {

    @Provides
    fun provideAuthRepository(authApiService: AuthApiService) = AuthRepository(authApiService)

    @Provides
    fun provideContactRepository(contactApiService: ContactApiService) = ContactRepository(contactApiService)

    @Provides
    fun provideDriverRepository(driverApiService: DriverApiService) = DriverRepository(driverApiService)

    @Provides
    fun provideRideRepository(rideApiService: RideApiService) = RideRepository(rideApiService)

    @Provides
    fun provideWeatherRepository(weatherApiService: WeatherApiService) = WeatherRepository(weatherApiService)
}