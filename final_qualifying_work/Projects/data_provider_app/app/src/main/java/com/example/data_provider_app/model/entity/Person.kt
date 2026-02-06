package com.example.data_provider_app.model.entity

import java.time.LocalDate

data class Person(
    val personId: UInt,
    val email: String,
    val phone: String,
    val lastName: String,
    val firstName: String,
    val patronymic: String?,
    val birth: LocalDate,
    val driveLisense: String?,
    val rightLevel: UByte
)
