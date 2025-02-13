package com.example.feature_playback_tracks.data.model

import com.google.gson.annotations.SerializedName

data class PlayerTrackResponse(
    val id: String,
    val title: String,
    val artist: ArtistResponse,
    val album: AlbumResponse,
    val duration: Int
)

data class ArtistResponse(
    val id: String,
    val name: String
)

data class AlbumResponse(
    val id: String,
    val title: String,
    @SerializedName("cover_big") val cover: String
)
