package com.example.feature_playback_tracks.data.repository

import com.example.feature_playback_tracks.data.api.DeezerApiService
import com.example.feature_playback_tracks.domain.model.Album
import com.example.feature_playback_tracks.domain.model.Artist
import com.example.feature_playback_tracks.domain.model.Track
import com.example.feature_playback_tracks.domain.repository.PlayerTrackRepository
import javax.inject.Inject

class PlayerTrackRepositoryImpl @Inject constructor(
    private val apiService: DeezerApiService
) : PlayerTrackRepository {
    override suspend fun getTrackById(trackId: String): Result<Track> {
        return try {
            val response = apiService.getTrackById(trackId)

            val track = Track(
                id = response.id,
                title = response.title,
                artist = Artist(
                    id = response.artist.id,
                    name = response.artist.name
                ),
                album = Album(
                    id = response.album.id,
                    title = response.album.title,
                    cover = response.album.cover
                ),
                duration = response.duration
            )

            Result.success(track)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

