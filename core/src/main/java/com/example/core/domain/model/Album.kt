package com.example.core.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Модель данных для альбома
 */
@Parcelize
data class Album(
    val id: String,
    val title: String,
    val cover: String,
    val releaseDate: String?
) : Parcelable