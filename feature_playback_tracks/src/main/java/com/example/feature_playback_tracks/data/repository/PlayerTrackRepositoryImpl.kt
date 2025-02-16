package com.example.feature_playback_tracks.data.repository

import com.example.core.data.mapper.TrackMapper
import com.example.core.data.api.DeezerApiService
import com.example.core.domain.model.Track
import com.example.feature_playback_tracks.domain.repository.PlayerTrackRepository
import javax.inject.Inject

class PlayerTrackRepositoryImpl @Inject constructor(
    private val apiService: DeezerApiService
) : PlayerTrackRepository {
    override suspend fun getTrackById(trackId: String): Result<Track> {
        return try {
            val response = apiService.getTrackById(trackId)
            val track = TrackMapper.mapTrack(response)
            Result.success(track)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}