package com.example.data_provider_app.dto

data class ValidationErrorDto(
    val type: String,
    val title: String,
    val status: UShort,
    val errors: Map<String, List<String>>,
    val traceId: String
)
