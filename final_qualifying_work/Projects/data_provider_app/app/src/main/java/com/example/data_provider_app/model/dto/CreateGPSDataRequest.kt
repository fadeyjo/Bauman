package com.example.data_provider_app.model.dto

data class CreateGPSDataRequest(
    val recDatetime: String,
    val tripId: ULong,
    val latitudeDEG: Float,
    val longitudeDEG: Float,
    val accuracyM: Float?,
    val speedKMH: UInt?,
    val bearingDEG: UShort?
)
