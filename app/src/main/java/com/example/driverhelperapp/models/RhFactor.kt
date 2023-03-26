package com.example.driverhelperapp.models

enum class RhFactor(val type: String) {
    NEGATIVE("Rh-"),
    POSITIVE("Rh+");

    companion object {
        fun fromType(type: String): RhFactor? {
            return RhFactor.values().find { it.type == type }
        }
    }
}
