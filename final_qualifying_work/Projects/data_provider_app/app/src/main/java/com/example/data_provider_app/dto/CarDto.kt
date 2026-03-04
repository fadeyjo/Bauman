package com.example.data_provider_app.dto

import java.time.LocalDateTime

data class CarDto(
    val carId: UInt,
    val createdAt: LocalDateTime,
    val personId: UInt,
    val vinNumber: String,
    val stateNumber: String?,
    val modelName: String,
    val brandName: String,
    val bodyName: String,
    val releaseYear: UShort,
    val gearboxName: String,
    val driveName: String,
    val enginePowerHP: UShort,
    val enginePowerKW: Float,
    val engineCapacityL: Float,
    val tankCapacityL: UByte,
    val fuelTypeName: String,
    val vehicleWeightKG: UShort,
    val photoId: UInt
)
