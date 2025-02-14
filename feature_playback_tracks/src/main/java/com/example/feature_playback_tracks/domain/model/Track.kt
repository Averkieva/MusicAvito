package com.example.feature_playback_tracks.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Track(
    val id: String,
    val title: String,
    val artist: Artist,
    val album: Album,
    val duration: Int,
    val preview: String,
    val trackPosition: Int,
) : Parcelable
