package com.example.data_provider_app.model.dto

data class CarResponse(
    val carId: UInt,
    val personId: UInt,
    val VINNumber: String,
    val stateNumber: String?,
    val bodyName: String,
    val releaseYear: UShort,
    val gearboxName: String,
    val driveName: String,
    val vehicleWeightKG: UShort,
    val brandName: String,
    val modelName: String,
    val enginePowerHP: UShort,
    val enginePowerKW: Float,
    val engineCapacityL: Float,
    val tankCapacityL: UByte,
    val engineTypeName: String,
    val fuelTypeName: String
)
