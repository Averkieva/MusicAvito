package com.example.feature_api_tracks.domain.search.model

data class Track(
    val id: String,
    val title: String,
    val artist: Artist,
    val album: Album,
    val duration: Int
)
