package com.example.feature_api_tracks.data.repository

import com.example.core.data.api.DeezerApiService
import com.example.core.domain.model.Track
import com.example.feature_api_tracks.domain.search.repository.ApiTrackRepository

class ApiTrackRepositoryImpl(private val apiService: DeezerApiService) : ApiTrackRepository {

    override suspend fun getTopTracks(): Result<List<Track>> {
        return try {
            val response = apiService.getTopTracks()
            val tracks = response.tracks.data
            if (tracks.isEmpty()) Result.failure(ApiTrackErrorHandler.getEmptyStateError())
            else Result.success(tracks)
        } catch (e: Exception) {
            Result.failure(ApiTrackErrorHandler.getError(e))
        }
    }

    override suspend fun searchTracks(query: String): Result<List<Track>> {
        return try {
            val response = apiService.searchTracks(query)
            val tracks = response.data
            if (tracks.isEmpty()) Result.failure(ApiTrackErrorHandler.getEmptyStateError())
            else Result.success(tracks)
        } catch (e: Exception) {
            Result.failure(ApiTrackErrorHandler.getError(e))
        }
    }
}

