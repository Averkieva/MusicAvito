package com.example.feature_playback_tracks.data.model

import com.google.gson.annotations.SerializedName

data class PlayerTrackResponse(
    val id: String,
    val title: String,
    val artist: ArtistResponse,
    val album: AlbumResponse,
    val duration: Int,
    val preview: String,
    @SerializedName("track_position") val trackPosition: Int,
)

data class ArtistResponse(
    val id: String,
    val name: String
)

data class AlbumResponse(
    val id: String,
    val title: String,
    @SerializedName("cover_big") val cover: String,
    @SerializedName("release_date") val releaseDate: String?
)
