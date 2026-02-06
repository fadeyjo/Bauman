package com.example.data_provider_app.model.dto

data class ValidationErrorResponse(
    val type: String?,
    val title: String?,
    val status: Int?,
    val errors: Map<String, List<String>>?
)

