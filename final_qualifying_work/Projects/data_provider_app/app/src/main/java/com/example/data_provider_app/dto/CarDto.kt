package com.example.data_provider_app.dto

data class CarDto(
    val carId: UInt,
    val personId: UInt,
    val VINNumber: String,
    val stateNumber: String?,
    val modelName: String,
    val brandName: String,
    val bodyName: String,
    val releaseYear: UShort,
    val gearboxName: String,
    val driveName: String,
    val enginePowerHP: UShort,
    val enginePowerKW: Float,
    val engineTypeName: String,
    val engineCapacityL: Float,
    val tankCapacityL: UByte,
    val fuelTypeName: String,
    val vehicleWeightKG: UShort
)
