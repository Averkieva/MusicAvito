package com.example.feature_api_tracks.di

import com.example.feature_api_tracks.ui.search.fragment.ApiTrackFragment
import dagger.Component

@FeatureScope
@Component(modules = [ViewModelModule::class])
interface FeatureComponent {
    fun inject(fragment: ApiTrackFragment)

    @Component.Factory
    interface Factory {
        fun create(): FeatureComponent
    }
}
