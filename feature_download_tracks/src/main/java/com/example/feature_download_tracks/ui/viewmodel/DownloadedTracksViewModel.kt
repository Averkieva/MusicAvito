package com.example.feature_download_tracks.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.feature_download_tracks.domain.repository.DownloadedTracksRepository
import com.example.feature_playback_tracks.domain.model.Track
import kotlinx.coroutines.launch
import javax.inject.Inject

class DownloadedTracksViewModel @Inject constructor(
    private val repository: DownloadedTracksRepository
) : ViewModel() {

    private val _downloadedTracks = MutableLiveData<List<Track>>()

    private val _searchResults = MutableLiveData<List<Track>>()
    val searchResults: LiveData<List<Track>> get() = _searchResults

    init {
        loadDownloadedTracks()
    }

    fun loadDownloadedTracks() {
        viewModelScope.launch {
            _downloadedTracks.value = repository.getDownloadedTracks()
            _searchResults.value = _downloadedTracks.value
        }
    }

    fun searchTracks(query: String) {
        viewModelScope.launch {
            _searchResults.value = repository.searchTracks(query)
        }
    }
}
