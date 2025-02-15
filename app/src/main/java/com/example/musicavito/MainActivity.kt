package com.example.musicavito

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.core.content.ContextCompat
import com.example.feature_playback_tracks.MediaPlaybackService

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        startPlaybackService()
    }

    private fun startPlaybackService() {
        val intent = Intent(this, MediaPlaybackService::class.java)
        ContextCompat.startForegroundService(this, intent)
    }
}
