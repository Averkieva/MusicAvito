package com.example.feature_api_tracks.data.repository

import com.example.core.data.api.DeezerApiService
import com.example.core.domain.model.Track
import com.example.feature_api_tracks.domain.search.repository.ApiTrackRepository

/**
 * Реализация `ApiTrackRepository`, которая взаимодействует с `DeezerApiService`
 * для загрузки списка треков (топовых и по запросу).
 *
 * Использует `Result`, чтобы обрабатывать как успешные, так и неуспешные ответы.
 *
 * @param apiService API-сервис Deezer для выполнения сетевых запросов.
 */
class ApiTrackRepositoryImpl(private val apiService: DeezerApiService) : ApiTrackRepository {

    /**
     * Загружает список топовых треков с сервера.
     *
     * @return `Result.success(List<Track>)`, если запрос успешен.
     * @return `Result.failure(ApiTrackErrorHandler.TrackError)`, если произошла ошибка или список пуст.
     */
    override suspend fun getTopTracks(): Result<List<Track>> {
        return try {
            val response = apiService.getTopTracks()
            val tracks = response.tracks.data

            if (tracks.isEmpty()) {
                Result.failure(ApiTrackErrorHandler.getEmptyStateError())
            } else {
                Result.success(tracks)
            }
        } catch (e: Exception) {
            Result.failure(ApiTrackErrorHandler.getError(e))
        }
    }

    /**
     * Выполняет поиск треков по заданному запросу.
     *
     * @param query Строка поиска.
     * @return `Result.success(List<Track>)`, если запрос успешен.
     * @return `Result.failure(ApiTrackErrorHandler.TrackError)`, если произошла ошибка или список пуст.
     */
    override suspend fun searchTracks(query: String): Result<List<Track>> {
        return try {
            val response = apiService.searchTracks(query)
            val tracks = response.data

            if (tracks.isEmpty()) {
                Result.failure(ApiTrackErrorHandler.getEmptyStateError())
            } else {
                Result.success(tracks)
            }
        } catch (e: Exception) {
            Result.failure(ApiTrackErrorHandler.getError(e))
        }
    }
}

