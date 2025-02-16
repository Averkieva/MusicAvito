package com.example.feature_playback_tracks

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.core.ui.viewmodel.SharedTrackViewModel
import com.example.feature_playback_tracks.data.repository.MediaPlayerRepositoryImpl
import com.example.feature_playback_tracks.domain.repository.MediaPlayerRepository

class MediaPlaybackService : Service() {

    private lateinit var mediaSession: MediaSessionCompat
    private var mediaPlayerRepository: MediaPlayerRepository? = null
    private lateinit var wakeLock: PowerManager.WakeLock

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        initMediaSession()
        mediaPlayerRepository = provideMediaPlayerRepository()

        mediaPlayerRepository?.isPlaying?.observeForever { isPlaying ->
            Log.d("MediaPlaybackService", "isPlaying changed: $isPlaying")
            updatePlaybackState()
        }

        startForeground(1, createNotification())

        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MediaPlaybackService::lock")
        wakeLock.acquire()
    }

    private fun initMediaSession() {
        mediaSession = MediaSessionCompat(this, "MediaPlaybackService").apply {
            setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS
            )
            setMediaButtonReceiver(null)
            setCallback(object : MediaSessionCompat.Callback() {
                override fun onPlay() {
                    Log.d("MediaSession", "onPlay() triggered")
                    mediaPlayerRepository?.togglePlayPause()
                    updatePlaybackState()
                }

                override fun onPause() {
                    Log.d("MediaSession", "onPause() triggered")
                    mediaPlayerRepository?.togglePlayPause()
                    updatePlaybackState()
                }
            })
        }
        mediaSession.isActive = true
    }

    private fun updatePlaybackState() {
        val isPlaying = mediaPlayerRepository?.isPlaying?.value ?: false
        val playbackState = PlaybackStateCompat.Builder()
            .setActions(
                PlaybackStateCompat.ACTION_PLAY or
                        PlaybackStateCompat.ACTION_PAUSE or
                        PlaybackStateCompat.ACTION_SKIP_TO_NEXT or
                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
            )
            .setState(
                if (isPlaying) PlaybackStateCompat.STATE_PLAYING else PlaybackStateCompat.STATE_PAUSED,
                mediaPlayerRepository?.getCurrentPosition()?.toLong() ?: 0L,
                if (isPlaying) 1.0f else 0f
            )
            .build()

        Log.d("MediaPlaybackService", "PlaybackState updated: ${if (isPlaying) "PLAYING" else "PAUSED"}")

        mediaSession.setPlaybackState(playbackState)
        startForeground(1, createNotification())
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("MediaPlaybackService", "onStartCommand() called with action: ${intent?.action}")

        intent?.action?.let {
            when (it) {
                ACTION_PLAY -> {
                    Log.d("MediaPlaybackService", "ACTION_PLAY received")
                    mediaSession.controller.transportControls.play()
                }
                ACTION_PAUSE -> {
                    Log.d("MediaPlaybackService", "ACTION_PAUSE received")
                    mediaSession.controller.transportControls.pause()
                }
            }
        }

        startForeground(1, createNotification())
        return START_STICKY
    }


    private fun createNotification(): Notification {
        val isPlaying = mediaPlayerRepository?.isPlaying?.value ?: false
        val playPauseIntent = Intent(this, MediaPlaybackReceiver::class.java).apply {
            action = if (isPlaying) ACTION_PAUSE else ACTION_PLAY
        }

        val playPausePendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            playPauseIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val playPauseAction = if (isPlaying) {
            NotificationCompat.Action(
                R.drawable.pause_button, "Pause",
                playPausePendingIntent
            )
        } else {
            NotificationCompat.Action(
                R.drawable.play_button, "Play",
                playPausePendingIntent
            )
        }

        return NotificationCompat.Builder(this, "music_channel")
            .setSmallIcon(R.drawable.ic_explicit)
            .setContentTitle("Музыкальный плеер")
            .setContentText(if (isPlaying) "Воспроизведение" else "Пауза")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .addAction(playPauseAction)
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(mediaSession.sessionToken)
                    .setShowActionsInCompactView(0)
            )
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "music_channel",
                "Музыка",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Канал для музыкального плеера"
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        mediaPlayerRepository?.togglePlayPause()
        mediaSession.release()
        wakeLock.release()
        super.onDestroy()
    }

    private fun provideMediaPlayerRepository(): MediaPlayerRepository {
        return MediaPlayerRepositoryImpl(SharedTrackViewModel(application))
    }

    companion object {
        const val ACTION_PLAY = "com.example.feature_playback_tracks.PLAY"
        const val ACTION_PAUSE = "com.example.feature_playback_tracks.PAUSE"
    }
}

class MediaPlaybackReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        context?.startService(Intent(context, MediaPlaybackService::class.java).apply {
            action = intent?.action
        })
    }
}

