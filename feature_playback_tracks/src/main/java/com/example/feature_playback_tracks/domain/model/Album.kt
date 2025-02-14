package com.example.feature_playback_tracks.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Album(
    val id: String,
    val title: String,
    val cover: String,
    val releaseDate: String?
) : Parcelable