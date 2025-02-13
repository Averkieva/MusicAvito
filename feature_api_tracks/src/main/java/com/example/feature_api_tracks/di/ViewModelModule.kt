package com.example.feature_api_tracks.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.feature_api_tracks.data.api.DeezerApiService
import com.example.feature_api_tracks.data.repository.ApiTrackRepositoryImpl
import com.example.feature_api_tracks.domain.search.repository.ApiTrackRepository
import com.example.feature_api_tracks.ui.search.viewmodel.ApiTrackViewModel
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
    fun provideApiTrackRepository(apiService: DeezerApiService): ApiTrackRepository {
        return ApiTrackRepositoryImpl(apiService)
    }

    @Provides
    fun provideApiTrackViewModel(trackRepository: ApiTrackRepository): ApiTrackViewModel {
        return ApiTrackViewModel(trackRepository)
    }

    @Provides
    fun provideViewModelFactory(viewModel: ApiTrackViewModel): ViewModelProvider.Factory {
        return object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(ApiTrackViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return viewModel as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}
