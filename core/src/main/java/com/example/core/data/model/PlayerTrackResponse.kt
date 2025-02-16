package com.example.core.data.model

import com.google.gson.annotations.SerializedName

/**
 * Модель ответа API для трека
 */
data class PlayerTrackResponse(
    val id: String,
    val title: String,
    val artist: ArtistResponse,
    val album: AlbumResponse,
    val duration: Int,
    val preview: String,
    @SerializedName("track_position") val trackPosition: Int,
)

/**
 * Модель ответа API для исполнителя
 */
data class ArtistResponse(
    val id: String,
    val name: String
)

/**
 * Модель ответа API для альбома
 */
data class AlbumResponse(
    val id: String,
    val title: String,
    @SerializedName("cover_big") val cover: String,
    @SerializedName("release_date") val releaseDate: String?
)
