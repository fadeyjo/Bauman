package com.example.data_provider_app.dto

import java.time.LocalDateTime

data class AvatarDto(
    val avatarId: UInt,
    val createdAt: LocalDateTime,
    val personId: UInt,
    val avatarUrl: String,
    val contentType: String
)
