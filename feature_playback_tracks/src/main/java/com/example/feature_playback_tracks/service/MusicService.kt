package com.example.feature_playback_tracks.service

import android.app.*
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import com.example.core.utils.TimeAndDateUtils.formatTimeFromMillis

/**
 * `MusicService` – фоновый сервис для управления воспроизведением музыки.
 * Использует `MediaPlayer` для воспроизведения аудио и отправляет обновления
 * о состоянии плеера через `Broadcast`.
 */
class MusicService : Service() {

    private var mediaPlayer: MediaPlayer? = null
    private val handler = Handler(Looper.getMainLooper())

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayer()
        createNotificationChannel()
    }

    /**
     * Обрабатывает команды управления воспроизведением, полученные через `Intent`.
     */
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_PAUSE -> pausePlayback()
            ACTION_RESUME -> resumePlayback()
            ACTION_STOP -> {
                stopPlayback()
                sendBroadcast(Intent(INTENT_SERVICE_STOPPED))
                stopSelf()
            }
            ACTION_SEEK_TO -> {
                val progress = intent.getIntExtra(EXTRA_SEEK_TO, 0)
                mediaPlayer?.seekTo(progress)
            }
            ACTION_START_BACKGROUND -> {
                val url = intent.getStringExtra(EXTRA_PREVIEW_URL)
                val position = intent.getIntExtra(EXTRA_CURRENT_POSITION, 0)

                if (url != null) {
                    startPlayback(url, position)
                }
            }
            ACTION_REQUEST_POSITION -> {
                val updateIntent = Intent(INTENT_UPDATE_POSITION).apply {
                    putExtra(EXTRA_CURRENT_POSITION, mediaPlayer?.currentPosition ?: 0)
                    putExtra(EXTRA_IS_PLAYING, mediaPlayer?.isPlaying ?: false)
                    putExtra(EXTRA_DURATION, mediaPlayer?.duration ?: 0)
                }
                sendBroadcast(updateIntent)
                stopSelf()
            }
        }
        return START_NOT_STICKY
    }

    /**
     * Очищает ресурсы при уничтожении сервиса.
     */
    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(updateProgressRunnable)
        mediaPlayer?.release()
        mediaPlayer = null
    }

    /**
     * Отправляет обновления о текущем положении воспроизведения через `Broadcast`.
     */
    private val updateProgressRunnable = object : Runnable {
        override fun run() {
            mediaPlayer?.let {
                val intent = Intent(INTENT_UPDATE_PROGRESS).apply {
                    putExtra(EXTRA_CURRENT_POSITION, it.currentPosition)
                    putExtra(EXTRA_DURATION, it.duration)
                }
                sendBroadcast(intent)
                showNotification(it.isPlaying)
            }
            handler.postDelayed(this, UPDATE_INTERVAL)
        }
    }

    /**
     * Останавливает воспроизведение и освобождает ресурсы `MediaPlayer`.
     */
    private fun stopPlayback() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        stopForeground(true)
        stopSelf()
    }

    /**
     * Начинает воспроизведение указанного аудиофайла.
     *
     * @param url URL аудиофайла.
     * @param startPosition Начальная позиция воспроизведения в миллисекундах.
     */
    private fun startPlayback(url: String, startPosition: Int = 0) {
        mediaPlayer?.reset()
        mediaPlayer?.setDataSource(url)
        mediaPlayer?.prepareAsync()
        mediaPlayer?.setOnPreparedListener {
            it.seekTo(startPosition)
            it.start()
            handler.post(updateProgressRunnable)
            sendPlaybackState(true)
            showNotification(true)
        }
    }

    /**
     * Ставит воспроизведение на паузу.
     */
    private fun pausePlayback() {
        mediaPlayer?.pause()
        handler.removeCallbacks(updateProgressRunnable)
        sendPlaybackState(false)
        showNotification(false)
    }

    /**
     * Возобновляет воспроизведение.
     */
    private fun resumePlayback() {
        mediaPlayer?.start()
        handler.post(updateProgressRunnable)
        sendPlaybackState(true)
        showNotification(true)
    }

    /**
     * Отправляет `Broadcast` о текущем состоянии воспроизведения.
     *
     * @param isPlaying `true`, если воспроизведение идет, иначе `false`.
     */
    private fun sendPlaybackState(isPlaying: Boolean) {
        val intent = Intent(INTENT_PLAYBACK_STATE).apply {
            putExtra(EXTRA_IS_PLAYING, isPlaying)
        }
        sendBroadcast(intent)
    }

    /**
     * Отображает уведомление с текущим состоянием воспроизведения.
     *
     * @param isPlaying `true`, если воспроизведение идет, иначе `false`.
     */
    private fun showNotification(isPlaying: Boolean) {
        val playPauseAction = if (isPlaying) {
            NotificationCompat.Action(
                android.R.drawable.ic_media_pause, "Pause",
                PendingIntent.getService(this, 0, Intent(this, MusicService::class.java).apply {
                    action = ACTION_PAUSE
                }, PendingIntent.FLAG_UPDATE_CURRENT)
            )
        } else {
            NotificationCompat.Action(
                android.R.drawable.ic_media_play, "Play",
                PendingIntent.getService(this, 0, Intent(this, MusicService::class.java).apply {
                    action = ACTION_RESUME
                }, PendingIntent.FLAG_UPDATE_CURRENT)
            )
        }

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Music Avito")
            .setContentText(
                formatTimeFromMillis(mediaPlayer?.currentPosition ?: 0) +
                        " / " + formatTimeFromMillis(mediaPlayer?.duration ?: 1)
            )
            .setSmallIcon(android.R.drawable.ic_media_play)
            .addAction(playPauseAction)
            .setOngoing(isPlaying)
            .setProgress(mediaPlayer?.duration ?: 1, mediaPlayer?.currentPosition ?: 0, false)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()

        startForeground(NOTIFICATION_ID, notification)
    }

    /**
     * Создает канал уведомлений для Android 8+.
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Music Service Channel",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    companion object {
        private const val CHANNEL_ID = "MusicServiceChannel"
        private const val NOTIFICATION_ID = 1
        private const val UPDATE_INTERVAL = 1000L

        private const val ACTION_PAUSE = "PAUSE"
        private const val ACTION_RESUME = "RESUME"
        private const val ACTION_STOP = "STOP"
        private const val ACTION_SEEK_TO = "SEEK_TO"
        private const val ACTION_START_BACKGROUND = "START_BACKGROUND"
        private const val ACTION_REQUEST_POSITION = "REQUEST_POSITION"

        private const val EXTRA_SEEK_TO = "SEEK_TO"
        private const val EXTRA_PREVIEW_URL = "PREVIEW_URL"
        private const val EXTRA_CURRENT_POSITION = "CURRENT_POSITION"
        private const val EXTRA_IS_PLAYING = "IS_PLAYING"
        private const val EXTRA_DURATION = "DURATION"

        private const val INTENT_UPDATE_POSITION = "UPDATE_POSITION"
        private const val INTENT_UPDATE_PROGRESS = "UPDATE_PROGRESS"
        private const val INTENT_PLAYBACK_STATE = "PLAYBACK_STATE"
        private const val INTENT_SERVICE_STOPPED = "SERVICE_STOPPED"
    }
}
