package com.example.driverhelperapp.repositories

import com.example.driverhelperapp.models.Driver
import com.example.driverhelperapp.services.DriverApiService
import com.example.driverhelperapp.utils.apiRequestFlow
import javax.inject.Inject

class DriverRepository @Inject constructor(
    private val driverApiService: DriverApiService,
) {
    fun getDriver() = apiRequestFlow {
        driverApiService.get()
    }

    fun updateDriver(driver: Driver) = apiRequestFlow {
        driverApiService.update(driver)
    }

    fun getInsuranceData() = apiRequestFlow {
        driverApiService.getInsuranceData()
    }

    fun updateInsuranceData(expirationDate: String, fileName: String) = apiRequestFlow {
        driverApiService.updateInsuranceData(expirationDate, fileName)
    }

    fun getMedicineData() = apiRequestFlow {
        driverApiService.getMedicineData()
    }

    fun updateMedicineData(expirationDate: String, fileName: String) = apiRequestFlow {
        driverApiService.updateMedicineData(expirationDate, fileName)
    }

    fun getVehicleMaintenanceData() = apiRequestFlow {
        driverApiService.getVehicleMaintenanceData()
    }

    fun updateVehicleMaintenanceData(expirationDate: String, fileName: String) = apiRequestFlow {
        driverApiService.updateVehicleMaintenanceData(expirationDate, fileName)
    }
}