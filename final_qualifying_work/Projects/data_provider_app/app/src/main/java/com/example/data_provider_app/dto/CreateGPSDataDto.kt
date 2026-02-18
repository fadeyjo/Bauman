package com.example.data_provider_app.dto

import java.time.LocalDateTime

data class CreateGPSDataDto(
    val recDatetime: LocalDateTime,
    val tripId: ULong,
    val latitudeDEG: Float,
    val longitudeDEG: Float,
    val accuracyM: Float?,
    val speedKMH: Int?,
    val bearingDEG: UShort?
)
