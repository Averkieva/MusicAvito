package com.example.feature_api_tracks.data.repository

import com.example.feature_api_tracks.domain.search.repository.ApiTrackRepository
import com.example.feature_playback_tracks.data.api.DeezerApiService
import com.example.feature_playback_tracks.domain.model.Album
import com.example.feature_playback_tracks.domain.model.Artist
import com.example.feature_playback_tracks.domain.model.Track

class ApiTrackRepositoryImpl(private val apiService: DeezerApiService) : ApiTrackRepository {

    override suspend fun getTopTracks(): Result<List<Track>> {
        return try {
            val response = apiService.getTopTracks()
            val tracks = response.tracks.data.map {
                Track(
                    id = it.id,
                    title = it.title,
                    artist = Artist(
                        id = it.artist.id,
                        name = it.artist.name
                    ),
                    album = Album(
                        id = it.album.id,
                        title = it.album.title,
                        cover = it.album.cover
                    ),
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
                    artist = Artist(
                        id = it.artist.id,
                        name = it.artist.name
                    ),
                    album = Album(
                        id = it.album.id,
                        title = it.album.title,
                        cover = it.album.cover
                    ),
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

