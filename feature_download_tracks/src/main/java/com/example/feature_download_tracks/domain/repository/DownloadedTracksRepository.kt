package com.example.feature_download_tracks.domain.repository

import com.example.feature_playback_tracks.domain.model.Track

interface DownloadedTracksRepository {
    fun getDownloadedTracks(): List<Track>
    fun searchTracks(query: String): List<Track>
}
