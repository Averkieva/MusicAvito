package com.example.feature_download_tracks.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.domain.model.Track
import com.example.feature_download_tracks.domain.repository.DownloadedTracksRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * `DownloadedTracksViewModel` управляет загруженными треками, предоставляет их в `LiveData`
 * и поддерживает поиск по локальному списку треков.
 *
 * @param repository Репозиторий, предоставляющий данные о загруженных треках.
 */
class DownloadedTracksViewModel @Inject constructor(
    private val repository: DownloadedTracksRepository
) : ViewModel() {

    /**
     * Полный список загруженных треков.
     * Используется внутри ViewModel, но не предоставляется напрямую во `LiveData`,
     * так как `searchResults` обновляется отдельно.
     */
    private val _downloadedTracks = MutableLiveData<List<Track>>()

    /**
     * `LiveData` с результатами поиска или всеми загруженными треками.
     * Используется во фрагменте для отображения списка треков.
     */
    private val _searchResults = MutableLiveData<List<Track>>()
    val searchResults: LiveData<List<Track>> get() = _searchResults

    init {
        loadDownloadedTracks()
    }

    /**
     * Загружает все загруженные треки из репозитория и обновляет `LiveData`.
     * Также устанавливает `_searchResults`, чтобы при загрузке сразу показывался весь список.
     */
    fun loadDownloadedTracks() {
        viewModelScope.launch {
            _downloadedTracks.value = repository.getDownloadedTracks()
            _searchResults.value = _downloadedTracks.value
        }
    }

    /**
     * Выполняет поиск треков по названию среди загруженных треков.
     * Если запрос пустой, возвращает весь список загруженных треков.
     *
     * @param query Поисковый запрос.
     */
    fun searchTracks(query: String) {
        viewModelScope.launch {
            _searchResults.value = repository.searchTracks(query)
        }
    }
}
