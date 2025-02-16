package com.example.feature_download_tracks.di

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.feature_download_tracks.data.repository.DownloadedTracksRepositoryImpl
import com.example.feature_download_tracks.domain.repository.DownloadedTracksRepository
import com.example.feature_download_tracks.ui.viewmodel.DownloadedTracksViewModel
import dagger.Module
import dagger.Provides

@Module
class ViewModelModule(private val context: Context) {

    @Provides
    fun provideDownloadedTracksRepository(): DownloadedTracksRepository {
        return DownloadedTracksRepositoryImpl(context)
    }
}

class DownloadedTracksViewModelFactory(private val repository: DownloadedTracksRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DownloadedTracksViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DownloadedTracksViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
