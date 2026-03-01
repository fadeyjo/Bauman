package com.example.data_provider_app.dto

import java.time.LocalDateTime

data class CarPhotoDto(
    val photoId: UInt,
    val createdAt: LocalDateTime,
    val carId: UInt,
    val photoUrl: String,
    val contentType: String
)
