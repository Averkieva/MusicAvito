package com.example.core.data.api

import com.example.core.data.model.ChartResponse
import com.example.core.data.model.PlayerTrackResponse
import com.example.core.data.model.SearchResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Интерфейс для взаимодействия с API Deezer
 */
interface DeezerApiService {
    @GET("chart")
    suspend fun getTopTracks(): ChartResponse

    @GET("search")
    suspend fun searchTracks(@Query("q") query: String): SearchResponse

    @GET("track/{trackId}")
    suspend fun getTrackById(@Path("trackId") trackId: String): PlayerTrackResponse
}
