package com.example.feature_api_tracks.ui.search.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.feature_api_tracks.data.repository.ApiTrackErrorHandler
import com.example.feature_api_tracks.domain.search.repository.ApiTrackRepository
import com.example.feature_playback_tracks.domain.model.Track
import kotlinx.coroutines.launch
import javax.inject.Inject

class ApiTrackViewModel @Inject constructor(
    private val trackRepository: ApiTrackRepository
) : ViewModel() {

    private val _tracks = MutableLiveData<List<Track>>()
    val tracks: LiveData<List<Track>> get() = _tracks

    private val _error = MutableLiveData<ApiTrackErrorHandler.TrackError?>()
    val error: LiveData<ApiTrackErrorHandler.TrackError?> get() = _error

    private var lastQuery: String? = null

    fun loadTopTracks() {
        lastQuery = null
        viewModelScope.launch {
            val result = trackRepository.getTopTracks()
            result.fold(
                onSuccess = { tracks ->
                    _tracks.value = tracks
                    _error.value = null
                },
                onFailure = { error ->
                    _error.value = error as? ApiTrackErrorHandler.TrackError
                }
            )
        }
    }

    fun searchTracks(query: String) {
        lastQuery = query
        viewModelScope.launch {
            val result = trackRepository.searchTracks(query)
            result.fold(
                onSuccess = { tracks ->
                    _tracks.value = tracks
                    _error.value = null
                },
                onFailure = { error ->
                    _error.value = error as? ApiTrackErrorHandler.TrackError
                }
            )
        }
    }

    fun restoreLastTracks() {
        if (lastQuery.isNullOrEmpty()) {
            loadTopTracks()
        } else {
            searchTracks(lastQuery!!)
        }
    }
}


