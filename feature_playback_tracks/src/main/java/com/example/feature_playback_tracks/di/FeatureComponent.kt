package com.example.feature_playback_tracks.di

import androidx.fragment.app.FragmentActivity
import com.example.feature_playback_tracks.ui.player.fragment.PlayerTrackFragment
import dagger.BindsInstance
import dagger.Component

@FeatureScope
@Component(modules = [ViewModelModule::class])
interface FeatureComponent {

    fun inject(fragment: PlayerTrackFragment)

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance activity: FragmentActivity): FeatureComponent
    }
}
