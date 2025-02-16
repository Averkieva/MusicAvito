package com.example.musicavito

import android.content.Intent
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.core.content.ContextCompat
import androidx.navigation.ui.setupWithNavController
import com.example.feature_playback_tracks.MediaPlaybackService
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.playerFragment) {
                bottomNavigationView.visibility = GONE
            } else {
                bottomNavigationView.visibility = VISIBLE
            }
        }

        startPlaybackService()
    }

    private fun startPlaybackService() {
        val intent = Intent(this, MediaPlaybackService::class.java)
        ContextCompat.startForegroundService(this, intent)
    }
}