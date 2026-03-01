package com.example.data_provider_app.dto

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDate
import java.time.LocalDateTime

@Parcelize
data class PersonDto(
    val personId: UInt,
    val createdAt: LocalDateTime,
    val email: String,
    val phone: String,
    val lastName: String,
    val firstName: String,
    val patronymic: String?,
    val birth: LocalDate,
    val driveLicense: String?,
    val avatarId: UInt
) : Parcelable
