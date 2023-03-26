package com.example.driverhelperapp.models

data class Driver(
    val bloodType: String?,
    val chronicDisease: String?,
    val contacts: List<Contact>?,
    val currentRide: Ride?,
    val enabled: Boolean?,
    val id: Long?,
    val insuranceData: InsuranceData?,
    val login: String?,
    val maxDriveDurationSeconds: Long?,
    val medicineData: MedicineData?,
    val middleName: String?,
    val myMedicines: String?,
    val name: String?,
    val phoneNumber: String?,
    val rhFactor: String?,
    val surname: String?,
    val vehicleMaintenanceData: VehicleMaintenanceData?
):java.io.Serializable
