package com.example.feature_api_tracks.ui.search.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.domain.model.Track
import com.example.feature_api_tracks.data.repository.ApiTrackErrorHandler
import com.example.feature_api_tracks.domain.search.repository.ApiTrackRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel для работы с API треков.
 * Позволяет загружать топовые треки, выполнять поиск и обрабатывать ошибки.
 *
 * @param trackRepository Репозиторий для загрузки данных о треках.
 */
class ApiTrackViewModel @Inject constructor(
    private val trackRepository: ApiTrackRepository
) : ViewModel() {

    private val _tracks = MutableLiveData<List<Track>>()
    val tracks: LiveData<List<Track>> get() = _tracks

    private val _error = MutableLiveData<ApiTrackErrorHandler.TrackError?>()
    val error: LiveData<ApiTrackErrorHandler.TrackError?> get() = _error

    /**
     * Последний выполненный поисковый запрос.
     * Если `null`, то показываются топовые треки.
     */
    private var lastQuery: String? = null

    /**
     * Загружает топовые треки и очищает предыдущий поисковый запрос.
     * В случае ошибки записывает ее в `_error`.
     */
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

    /**
     * Выполняет поиск треков по запросу.
     * В случае ошибки записывает ее в `_error`.
     *
     * @param query Поисковый запрос.
     */
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

    /**
     * Восстанавливает список треков после изменения состояния (например, после поворота экрана).
     * Если был выполнен поиск, то повторяет его, иначе загружает топовые треки.
     */
    fun restoreLastTracks() {
        if (lastQuery.isNullOrEmpty()) {
            loadTopTracks()
        } else {
            searchTracks(lastQuery!!)
        }
    }

    /**
     * Проверяет, находится ли пользователь в режиме поиска.
     *
     * @return `true`, если выполнялся поиск, иначе `false`.
     */
    fun isSearchMode(): Boolean {
        return !lastQuery.isNullOrEmpty()
    }
}


