package com.example.data_provider_app.dto

data class ErrorDto(
    val type: String,
    val title: String,
    val status: UShort,
    val traceId: String
)
