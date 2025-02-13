package com.example.feature_playback_tracks.ui.player.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.feature_playback_tracks.domain.model.Track
import com.example.feature_playback_tracks.domain.repository.PlayerTrackRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

class PlayerTrackViewModel @Inject constructor(
    private val repository: PlayerTrackRepository
) : ViewModel() {

    private val _track = MutableLiveData<Track?>()
    val track: LiveData<Track?> get() = _track

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    fun loadTrack(trackId: String) {
        viewModelScope.launch {
            val result = repository.getTrackById(trackId)
            result.fold(
                onSuccess = { _track.value = it },
                onFailure = { _error.value = "Ошибка загрузки трека" }
            )
        }
    }
}
