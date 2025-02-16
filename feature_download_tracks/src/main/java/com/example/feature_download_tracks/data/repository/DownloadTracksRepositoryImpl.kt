package com.example.feature_download_tracks.data.repository

import android.content.Context
import com.example.core.domain.model.Track
import com.example.feature_download_tracks.domain.repository.DownloadedTracksRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import javax.inject.Inject

/**
 * Реализация `DownloadedTracksRepository`, которая управляет загруженными треками,
 * сохраняя и извлекая их из `SharedPreferences`.
 *
 * @param context Контекст приложения, необходимый для доступа к `SharedPreferences`.
 */
class DownloadedTracksRepositoryImpl @Inject constructor(
    context: Context
) : DownloadedTracksRepository {

    private val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()

    /**
     * Получает список загруженных треков из `SharedPreferences`.
     *
     * @return Список треков или пустой список, если данных нет.
     */
    override fun getDownloadedTracks(): List<Track> {
        val json = sharedPreferences.getString(KEY_TRACKS, null)
        return if (json != null) {
            val type = object : TypeToken<List<Track>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } else {
            emptyList()
        }
    }

    /**
     * Выполняет поиск загруженных треков по названию.
     *
     * @param query Строка поиска.
     * @return Список треков, соответствующих запросу, или все треки, если запрос пустой.
     */
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

