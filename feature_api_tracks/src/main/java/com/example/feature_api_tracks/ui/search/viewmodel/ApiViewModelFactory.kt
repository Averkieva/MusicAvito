package com.example.feature_api_tracks.ui.search.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.feature_api_tracks.domain.search.repository.ApiTrackRepository

class ApiViewModelFactory(private val trackRepository: ApiTrackRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ApiTrackViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ApiTrackViewModel(trackRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}