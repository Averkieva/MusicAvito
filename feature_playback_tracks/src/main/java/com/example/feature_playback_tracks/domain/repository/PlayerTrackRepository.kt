package com.example.feature_playback_tracks.domain.repository

import com.example.core.domain.model.Track

interface PlayerTrackRepository {
    suspend fun getTrackById(trackId: String): Result<Track>
}