package com.example.feature_playback_tracks.data.repository

import com.example.core.data.mapper.TrackMapper
import com.example.core.data.api.DeezerApiService
import com.example.core.domain.model.Track
import com.example.feature_playback_tracks.domain.repository.PlayerTrackRepository
import javax.inject.Inject

/**
 * Реализация `PlayerTrackRepository`, которая загружает информацию о треке по его ID
 * с помощью `DeezerApiService`.
 *
 * @param apiService API-сервис Deezer для выполнения сетевых запросов.
 */
class PlayerTrackRepositoryImpl @Inject constructor(
    private val apiService: DeezerApiService
) : PlayerTrackRepository {

    /**
     * Загружает трек по его `trackId`.
     *
     * @param trackId Уникальный идентификатор трека.
     * @return `Result.success(Track)`, если запрос успешен.
     * @return `Result.failure(Exception)`, если произошла ошибка.
     */
    override suspend fun getTrackById(trackId: String): Result<Track> {
        return try {
            val response = apiService.getTrackById(trackId)
            val track = TrackMapper.mapTrack(response) // Преобразует DTO в доменную модель
            Result.success(track)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}