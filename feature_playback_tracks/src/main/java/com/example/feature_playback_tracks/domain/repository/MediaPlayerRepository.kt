package com.example.feature_playback_tracks.domain.repository

import androidx.lifecycle.LiveData

interface MediaPlayerRepository {
    val isPlaying: LiveData<Boolean>

    fun prepareMediaPlayer(previewUrl: String, autoPlay: Boolean = true)
    fun togglePlayPause()
    fun releasePlayer()
    fun skipToNext()
    fun skipToPrevious()
    fun getCurrentPosition(): Int
    fun seekTo(position: Int)
}