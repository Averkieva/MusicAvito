package com.example.feature_playback_tracks.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Artist(
    val id: String,
    val name: String
) : Parcelable