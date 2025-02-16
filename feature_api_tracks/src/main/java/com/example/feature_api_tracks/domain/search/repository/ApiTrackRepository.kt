package com.example.feature_api_tracks.domain.search.repository

import com.example.core.domain.model.Track


interface ApiTrackRepository {
    suspend fun getTopTracks(): Result<List<Track>>
    suspend fun searchTracks(query: String): Result<List<Track>>
}

