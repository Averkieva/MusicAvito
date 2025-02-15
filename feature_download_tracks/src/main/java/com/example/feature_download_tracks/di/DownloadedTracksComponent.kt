package com.example.feature_download_tracks.di

import com.example.feature_download_tracks.ui.fragment.DownloadedTracksFragment
import dagger.Component

@Component(modules = [ViewModelModule::class])
interface DownloadedTracksComponent {
    fun inject(fragment: DownloadedTracksFragment)
}
