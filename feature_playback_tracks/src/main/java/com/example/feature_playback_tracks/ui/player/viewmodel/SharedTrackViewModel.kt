package com.example.feature_playback_tracks.ui.player.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.feature_playback_tracks.domain.model.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import javax.inject.Inject

class SharedTrackViewModel @Inject constructor(application: Application) : AndroidViewModel(application) {

    private val gson = Gson()
    private val sharedPreferences = application.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    private val _searchTracks = MutableLiveData<List<Track>>(emptyList())
    private val _topTracks = MutableLiveData<List<Track>>(emptyList())
    private val _downloadedTracks = MutableLiveData<List<Track>>(emptyList())
    private val _filteredDownloadedTracks = MutableLiveData<List<Track>>(emptyList())

    private val _currentTrackId = MutableLiveData<String?>()
    val currentTrackId: LiveData<String?> get() = _currentTrackId

    private val _currentPlaylistType = MutableLiveData<String?>()

    init {
        loadDownloadedTracks()
    }

    fun setSearchTracks(tracks: List<Track>) {
        _searchTracks.value = tracks
        setCurrentPlaylistType(PLAYLIST_SEARCH)
    }

    fun setTopTracks(tracks: List<Track>) {
        _topTracks.value = tracks
        setCurrentPlaylistType(PLAYLIST_TOP)
    }

    private fun setDownloadedTracks(tracks: List<Track>) {
        _downloadedTracks.value = tracks
        saveDownloadedTracks(tracks)
        setCurrentPlaylistType(PLAYLIST_DOWNLOADED)
    }

    fun setFilteredDownloadedTracks(tracks: List<Track>) {
        _filteredDownloadedTracks.value = tracks
        setCurrentPlaylistType(PLAYLIST_FILTERED_DOWNLOADED)
    }

    private fun setCurrentPlaylistType(type: String) {
        _currentPlaylistType.value = type
    }

    fun setCurrentTrack(trackId: String) {
        if (trackId.isNotBlank()) {
            _currentTrackId.value = trackId
        }
    }

    fun getNextTrack(): Track? {
        val currentList = getTrackListByType() ?: return null
        val currentId = _currentTrackId.value ?: return currentList.firstOrNull()
        val currentIndex = currentList.indexOfFirst { it.id == currentId }

        return if (currentIndex in 0 until currentList.lastIndex) currentList[currentIndex + 1] else null
    }

    fun getPreviousTrack(): Track? {
        val currentList = getTrackListByType() ?: return null
        val currentId = _currentTrackId.value ?: return null
        val currentIndex = currentList.indexOfFirst { it.id == currentId }

        return if (currentIndex > 0) currentList[currentIndex - 1] else null
    }

    fun addTrack(track: Track) {
        val currentList = _downloadedTracks.value.orEmpty()
        if (track.id !in currentList.map { it.id }) {
            val updatedList = currentList + track
            _downloadedTracks.postValue(updatedList)
            saveDownloadedTracks(updatedList)
        }
    }

    private fun saveDownloadedTracks(tracks: List<Track>) {
        sharedPreferences.edit()
            .putString(TRACKS_KEY, gson.toJson(tracks))
            .apply()
    }

    private fun loadDownloadedTracks() {
        sharedPreferences.getString(TRACKS_KEY, null)?.let { json ->
            val type = object : TypeToken<List<Track>>() {}.type
            val tracks: List<Track> = gson.fromJson(json, type)
            _downloadedTracks.postValue(tracks)
            setDownloadedTracks(tracks)
        }
    }

    private fun getTrackListByType(): List<Track>? {
        return when (_currentPlaylistType.value) {
            PLAYLIST_SEARCH -> _searchTracks.value
            PLAYLIST_TOP -> _topTracks.value
            PLAYLIST_DOWNLOADED -> _downloadedTracks.value
            PLAYLIST_FILTERED_DOWNLOADED -> _filteredDownloadedTracks.value
            else -> null
        }
    }

    fun isTrackDownloaded(trackId: String): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        result.value = _downloadedTracks.value?.any { it.id == trackId } == true
        return result
    }

    companion object {
        private const val PREF_NAME = "downloaded_tracks"
        private const val TRACKS_KEY = "tracks"

        private const val PLAYLIST_SEARCH = "searchTracks"
        private const val PLAYLIST_TOP = "topTracks"
        private const val PLAYLIST_DOWNLOADED = "downloadedTracks"
        private const val PLAYLIST_FILTERED_DOWNLOADED = "filteredDownloadedTracks"
    }
}
