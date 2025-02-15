package com.example.feature_playback_tracks.data.api

import com.example.feature_playback_tracks.data.model.ChartResponse
import com.example.feature_playback_tracks.data.model.PlayerTrackResponse
import com.example.feature_playback_tracks.data.model.SearchResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface DeezerApiService {
    @GET("chart")
    suspend fun getTopTracks(): ChartResponse

    @GET("search")
    suspend fun searchTracks(@Query("q") query: String): SearchResponse

    @GET("track/{trackId}")
    suspend fun getTrackById(@Path("trackId") trackId: String): PlayerTrackResponse
}
