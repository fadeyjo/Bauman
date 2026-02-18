package com.example.data_provider_app.dto

data class UpdateCarInfoDto(
    val carId: UInt,
    val VINNumber: String,
    val stateNumber: String?,
    val brandName: String,
    val modelName: String,
    val bodyName: String,
    val releaseYear: UShort,
    val gearboxName: String,
    val driveName: String,
    val vehicleWeightKG: UShort,
    val enginePowerHP: UShort,
    val enginePowerKW: Float,
    val engineCapacityL: Float,
    val engineTypeName: String,
    val tankCapacityL: UByte,
    val fuelTypeName: String
)
