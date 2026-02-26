package com.example.data_provider_app.dto

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDate

@Parcelize
data class PersonDto(
    val personId: UInt,
    val email: String,
    val phone: String,
    val lastName: String,
    val firstName: String,
    val patronymic: String?,
    val birth: LocalDate,
    val driveLisense: String?
) : Parcelable
