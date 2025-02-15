package com.example.feature_playback_tracks.ui.player.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.feature_playback_tracks.domain.model.Track
import javax.inject.Inject

class SharedTrackViewModel @Inject constructor() : ViewModel() {

    private val _trackList = MutableLiveData<List<Track>>(emptyList())

    private val _currentTrackId = MutableLiveData<String?>()
    val currentTrackId: LiveData<String?> get() = _currentTrackId

    fun setTrackList(tracks: List<Track>) {
        _trackList.value = tracks
    }

    fun setCurrentTrack(trackId: String) {
        if (trackId.isBlank()) {
            return
        }
        _currentTrackId.value = trackId
    }

    fun getNextTrack(): Track? {
        val tracks = _trackList.value

        if (tracks.isNullOrEmpty()) {
            return null
        }

        val currentId = _currentTrackId.value ?: return tracks.firstOrNull()

        val currentIndex = tracks.indexOfFirst { it.id == currentId }
        if (currentIndex == -1) {
            return null
        }

        val nextTrack = if (currentIndex < tracks.lastIndex) tracks[currentIndex + 1] else null
        return nextTrack
    }

    fun getPreviousTrack(): Track? {
        val tracks = _trackList.value ?: return null
        val currentId = _currentTrackId.value ?: return null
        val currentIndex = tracks.indexOfFirst { it.id == currentId }
        return if (currentIndex > 0) tracks[currentIndex - 1] else null
    }
}
