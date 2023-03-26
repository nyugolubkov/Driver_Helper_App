package com.example.driverhelperapp.services

import com.example.driverhelperapp.models.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Query

interface DriverApiService {
    @GET("/driver")
    suspend fun get(): Response<Driver>

    @PUT("/driver")
    suspend fun update(
        @Body driver: Driver
    ): Response<Driver>

    @GET("/driver/insurance")
    suspend fun getInsuranceData(): Response<InsuranceData>

    @PUT("/driver/insurance")
    suspend fun updateInsuranceData(
        @Query("expirationDate") expirationDate: String,
        @Query("fileName") fileName: String
    ): Response<InsuranceData>

    @GET("/driver/medicine")
    suspend fun getMedicineData(): Response<MedicineData>

    @PUT("/driver/medicine")
    suspend fun updateMedicineData(
        @Query("expirationDate") expirationDate: String,
        @Query("fileName") fileName: String
    ): Response<MedicineData>

    @GET("/driver/vehicle_maintenance")
    suspend fun getVehicleMaintenanceData(): Response<VehicleMaintenanceData>

    @PUT("/driver/vehicle_maintenance")
    suspend fun updateVehicleMaintenanceData(
        @Query("expirationDate") expirationDate: String,
        @Query("fileName") fileName: String
    ): Response<VehicleMaintenanceData>
}