package com.example.feature_playback_tracks.ui.player.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.feature_playback_tracks.domain.model.Track
import com.example.feature_playback_tracks.domain.repository.MediaPlayerRepository
import com.example.feature_playback_tracks.domain.repository.PlayerTrackRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class PlayerTrackViewModel @Inject constructor(
    private val trackRepository: PlayerTrackRepository,
    private val mediaPlayerRepository: MediaPlayerRepository
) : ViewModel() {

    private val _track = MutableLiveData<Track?>()
    val track: LiveData<Track?> get() = _track
    val isPlaying: LiveData<Boolean> get() = mediaPlayerRepository.isPlaying

    private val _trackProgress = MutableLiveData<Int>()
    val trackProgress: LiveData<Int> get() = _trackProgress

    private val _trackDuration = MutableLiveData<Int>()
    val trackDuration: LiveData<Int> get() = _trackDuration

    private var progressJob: Job? = null

    fun loadTrack(trackId: String, autoPlay: Boolean = true) {
        viewModelScope.launch {
            val result = trackRepository.getTrackById(trackId)
            result.fold(
                onSuccess = { track ->
                    _track.value = track
                    _trackDuration.value = (track.duration * MILLISECONDS_IN_SECOND).coerceAtMost(MAX_TRACK_DURATION_MS)
                    mediaPlayerRepository.prepareMediaPlayer(track.preview, autoPlay)
                    startTrackingProgress()
                },
                onFailure = { error ->
                    // Обработка ошибки
                }
            )
        }
    }

    fun togglePlayPause() {
        mediaPlayerRepository.togglePlayPause()
    }

    fun skipToNext() {
        mediaPlayerRepository.skipToNext()
    }

    fun skipToPrevious() {
        mediaPlayerRepository.skipToPrevious()
    }

    fun seekTo(progress: Int) {
        mediaPlayerRepository.seekTo(progress)
        _trackProgress.value = progress
    }

    private fun startTrackingProgress() {
        progressJob?.cancel()
        progressJob = viewModelScope.launch {
            while (true) {
                val position = mediaPlayerRepository.getCurrentPosition()
                _trackProgress.postValue(position.coerceAtMost(_trackDuration.value ?: MAX_TRACK_DURATION_MS))
                delay(PROGRESS_UPDATE_INTERVAL_MS)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayerRepository.releasePlayer()
    }

    companion object {
        private const val MAX_TRACK_DURATION_MS = 30_000
        private const val PROGRESS_UPDATE_INTERVAL_MS = 500L
        private const val MILLISECONDS_IN_SECOND = 1_000
    }
}
