package com.example.driverhelperapp.models

data class Ride(
    val finishedTime: String?,
    val id: Long?,
    val needRestNotificationSent: Boolean,
    val startTime: String
):java.io.Serializable
