package com.example.feature_api_tracks.data.repository

import android.util.Log
import com.example.feature_api_tracks.data.api.DeezerApiService
import com.example.feature_api_tracks.domain.search.model.Album
import com.example.feature_api_tracks.domain.search.model.Artist
import com.example.feature_api_tracks.domain.search.model.Track
import com.example.feature_api_tracks.domain.search.repository.ApiTrackRepository

class ApiTrackRepositoryImpl(private val apiService: DeezerApiService) : ApiTrackRepository {

    override suspend fun getTopTracks(): Result<List<Track>> {
        return try {
            val response = apiService.getTopTracks()
            val tracks = response.tracks.data.map {
                Track(
                    id = it.id,
                    title = it.title,
                    artist = Artist(it.artist.name),
                    album = Album(it.album.cover),
                    duration = it.duration
                )
            }
            if (tracks.isEmpty()) Result.failure(ApiTrackErrorHandler.getEmptyStateError())
            else Result.success(tracks)
        } catch (e: Exception) {
            Result.failure(ApiTrackErrorHandler.getError(e))
        }
    }

    override suspend fun searchTracks(query: String): Result<List<Track>> {
        return try {
            val response = apiService.searchTracks(query)
            val tracks = response.data.map {
                Track(
                    id = it.id,
                    title = it.title,
                    artist = Artist(it.artist.name),
                    album = Album(it.album.cover),
                    duration = it.duration
                )
            }
            if (tracks.isEmpty()) Result.failure(ApiTrackErrorHandler.getEmptyStateError())
            else Result.success(tracks)
        } catch (e: Exception) {
            Result.failure(ApiTrackErrorHandler.getError(e))
        }
    }
}

