package com.example.core.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Модель данных для исполнителя
 */
@Parcelize
data class Artist(
    val id: String,
    val name: String
) : Parcelable