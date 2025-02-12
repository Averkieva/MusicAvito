package com.example.feature_api_tracks.data.api

import com.example.feature_api_tracks.data.model.ChartResponse
import com.example.feature_api_tracks.data.model.SearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface DeezerApiService {
    @GET("chart")
    suspend fun getTopTracks(): ChartResponse

    @GET("search")
    suspend fun searchTracks(@Query("q") query: String): SearchResponse
}
