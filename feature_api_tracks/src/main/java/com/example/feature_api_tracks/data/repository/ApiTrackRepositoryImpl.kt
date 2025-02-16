package com.example.feature_api_tracks.data.repository

import com.example.core.data.api.DeezerApiService
import com.example.core.utils.TimeAndDateUtils.formatReleaseDate
import com.example.core.domain.model.Album
import com.example.core.domain.model.Artist
import com.example.core.domain.model.Track
import com.example.feature_api_tracks.domain.search.repository.ApiTrackRepository

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
                        cover = it.album.cover,
                        releaseDate = formatReleaseDate(it.album.releaseDate)
                    ),
                    duration = it.duration,
                    preview = it.preview,
                    trackPosition = it.trackPosition
                )
            }
            //To Do create mapper
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
                        cover = it.album.cover,
                        releaseDate = formatReleaseDate(it.album.releaseDate)
                    ),
                    duration = it.duration,
                    preview = it.preview,
                    trackPosition = it.trackPosition
                )
            }
            if (tracks.isEmpty()) Result.failure(ApiTrackErrorHandler.getEmptyStateError())
            else Result.success(tracks)
        } catch (e: Exception) {
            Result.failure(ApiTrackErrorHandler.getError(e))
        }
    }
}

