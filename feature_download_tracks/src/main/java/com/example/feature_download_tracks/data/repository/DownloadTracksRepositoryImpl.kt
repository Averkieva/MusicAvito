package com.example.feature_download_tracks.data.repository

import android.content.Context
import com.example.core.domain.model.Track
import com.example.feature_download_tracks.domain.repository.DownloadedTracksRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import javax.inject.Inject

class DownloadedTracksRepositoryImpl @Inject constructor(
    context: Context
) : DownloadedTracksRepository {

    private val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()

    override fun getDownloadedTracks(): List<Track> {
        val json = sharedPreferences.getString(KEY_TRACKS, null)
        return if (json != null) {
            val type = object : TypeToken<List<Track>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } else {
            emptyList()
        }
    }

    override fun searchTracks(query: String): List<Track> {
        val allTracks = getDownloadedTracks()
        return if (query.isBlank()) {
            allTracks
        } else {
            allTracks.filter { it.title.contains(query, ignoreCase = true) }
        }
    }

    companion object {
        private const val PREF_NAME = "downloaded_tracks"
        private const val KEY_TRACKS = "tracks"
    }
}

