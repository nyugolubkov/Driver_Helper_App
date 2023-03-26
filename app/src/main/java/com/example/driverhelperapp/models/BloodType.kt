package com.example.driverhelperapp.models

enum class BloodType(val type: String) {
    AB_4("AB (IV)"),
    A_2("A (II)"),
    B_3("B (III)"),
    O_1("O (I)");

    companion object {
        fun fromType(type: String): BloodType? {
            return values().find { it.type == type }
        }
    }
}