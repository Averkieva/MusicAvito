package com.example.feature_playback_tracks.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.feature_playback_tracks.data.api.DeezerApiService
import com.example.feature_playback_tracks.data.repository.PlayerTrackRepositoryImpl
import com.example.feature_playback_tracks.domain.repository.PlayerTrackRepository
import com.example.feature_playback_tracks.ui.player.viewmodel.PlayerTrackViewModel
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
class ViewModelModule {

    @Provides
    fun provideDeezerApiService(): DeezerApiService {
        return Retrofit.Builder()
            .baseUrl("https://api.deezer.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DeezerApiService::class.java)
    }

    @Provides
    fun providePlayerTrackRepository(apiService: DeezerApiService): PlayerTrackRepository {
        return PlayerTrackRepositoryImpl(apiService)
    }

    @Provides
    fun providePlayerTrackViewModel(trackRepository: PlayerTrackRepository): PlayerTrackViewModel {
        return PlayerTrackViewModel(trackRepository)
    }

    @Provides
    fun provideViewModelFactory(viewModel: PlayerTrackViewModel): ViewModelProvider.Factory {
        return object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(PlayerTrackViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return viewModel as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}