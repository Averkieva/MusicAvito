package com.example.feature_playback_tracks.ui.player.viewmodel

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.*
import com.example.core.domain.model.Track
import com.example.feature_playback_tracks.service.MusicService
import com.example.feature_playback_tracks.R
import com.example.feature_playback_tracks.domain.repository.MediaPlayerRepository
import com.example.feature_playback_tracks.domain.repository.PlayerTrackRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * `PlayerTrackViewModel` управляет логикой воспроизведения трека, взаимодействует с `MediaPlayerRepository`
 * и отслеживает состояние приложения (фоновый/передний план).
 *
 * @param appContext Контекст приложения, используется для запуска сервисов и отправки уведомлений.
 * @param trackRepository Репозиторий для загрузки информации о треке.
 * @param mediaPlayerRepository Репозиторий, управляющий воспроизведением треков.
 */
class PlayerTrackViewModel @Inject constructor(
    private val appContext: Context,
    private val trackRepository: PlayerTrackRepository,
    private val mediaPlayerRepository: MediaPlayerRepository
) : ViewModel(), LifecycleObserver {

    /**
     * `LiveData`, содержащая текущий воспроизводимый трек.
     */
    private val _track = MutableLiveData<Track?>()
    val track: LiveData<Track?> get() = _track

    /**
     * `LiveData`, отслеживающая состояние воспроизведения (играет/не играет).
     */
    val isPlaying: LiveData<Boolean> get() = mediaPlayerRepository.isPlaying

    /**
     * `LiveData`, содержащая текущее положение воспроизведения.
     */
    private val _trackProgress = MutableLiveData<Int>()
    val trackProgress: LiveData<Int> get() = _trackProgress

    /**
     * `LiveData`, содержащая длительность трека.
     */
    private val _trackDuration = MutableLiveData<Int>()
    val trackDuration: LiveData<Int> get() = _trackDuration

    private var isBackgroundMode = false
    private var _wasPlayingInBackground = false

    init {
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    /**
     * Загружает трек по `trackId` и подготавливает `MediaPlayer`.
     *
     * @param trackId ID трека.
     * @param autoPlay `true`, если трек должен начать воспроизводиться автоматически.
     */
    @SuppressLint("StringFormatInvalid")
    fun loadTrack(trackId: String, autoPlay: Boolean = true) {
        viewModelScope.launch {
            val result = trackRepository.getTrackById(trackId)
            result.fold(
                onSuccess = { track ->
                    _track.value = track
                    _trackDuration.value = (track.duration * MILLISECONDS_IN_SECOND)
                        .coerceAtMost(MAX_TRACK_DURATION_MS)
                    mediaPlayerRepository.prepareMediaPlayer(track.preview, autoPlay)
                    startTrackingProgress()
                },
                onFailure = { error ->
                    Toast.makeText(
                        appContext,
                        appContext.getString(R.string.track_download_error, error.message),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            )
        }
    }

    /** Переключает состояние воспроизведения (играет/пауза). */
    fun togglePlayPause() {
        mediaPlayerRepository.togglePlayPause()
    }

    /** Перематывает трек вперед на 10 секунд. */
    fun skipToNext() {
        mediaPlayerRepository.skipToNext()
    }

    /** Перематывает трек назад на 10 секунд. */
    fun skipToPrevious() {
        mediaPlayerRepository.skipToPrevious()
    }

    /**
     * Перематывает трек на заданную позицию.
     *
     * @param progress Новая позиция воспроизведения в миллисекундах.
     */
    fun seekTo(progress: Int) {
        mediaPlayerRepository.seekTo(progress)
        _trackProgress.value = progress
    }

    /** Запускает обновление прогресса воспроизведения трека. */
    private fun startTrackingProgress() {
        viewModelScope.launch {
            while (true) {
                val position = mediaPlayerRepository.getCurrentPosition()
                _trackProgress.postValue(position.coerceAtMost(_trackDuration.value ?: MAX_TRACK_DURATION_MS))
                delay(PROGRESS_UPDATE_INTERVAL_MS)
            }
        }
    }

    /**
     * Обрабатывает переход приложения в фоновый режим.
     * Останавливает `MediaPlayer` и передает воспроизведение сервису `MusicService`.
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onAppBackgrounded() {
        if (isPlaying.value == true) {
            isBackgroundMode = true

            val currentPosition = mediaPlayerRepository.getCurrentPosition()

            val intent = Intent(appContext, MusicService::class.java).apply {
                action = ACTION_START_BACKGROUND
                putExtra(EXTRA_PREVIEW_URL, _track.value?.preview)
                putExtra(EXTRA_CURRENT_POSITION, currentPosition)
            }
            appContext.startService(intent)

            mediaPlayerRepository.togglePlayPause()
        }
    }

    /**
     * Обрабатывает возвращение приложения в передний план.
     * Запрашивает текущую позицию воспроизведения у `MusicService`.
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onAppForegrounded() {
        if (isBackgroundMode) {
            isBackgroundMode = false

            val intent = Intent(appContext, MusicService::class.java).apply {
                action = ACTION_REQUEST_POSITION
            }
            appContext.startService(intent)
        }
    }

    /**
     * Получает обновления от `MusicService` о текущем положении воспроизведения и статусе плеера.
     */
    private val positionReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                ACTION_UPDATE_POSITION -> {
                    val position = intent.getIntExtra(EXTRA_CURRENT_POSITION, 0)
                    val wasPlaying = intent.getBooleanExtra(EXTRA_IS_PLAYING, false)

                    mediaPlayerRepository.seekTo(position)
                    _wasPlayingInBackground = wasPlaying

                    val stopIntent = Intent(appContext, MusicService::class.java).apply {
                        action = ACTION_STOP
                    }
                    appContext.startService(stopIntent)
                }
                ACTION_SERVICE_STOPPED -> {
                    if (_wasPlayingInBackground) {
                        mediaPlayerRepository.togglePlayPause()
                    }
                }
                ACTION_UPDATE_PROGRESS -> {
                    val position = intent.getIntExtra(EXTRA_CURRENT_POSITION, 0)
                    val duration = intent.getIntExtra(EXTRA_DURATION, 0)

                    _trackProgress.postValue(position)
                    _trackDuration.postValue(duration)
                }
            }
        }
    }

    init {
        val filter = IntentFilter().apply {
            addAction(ACTION_UPDATE_POSITION)
            addAction(ACTION_SERVICE_STOPPED)
            addAction(ACTION_UPDATE_PROGRESS)
        }
        ContextCompat.registerReceiver(
            appContext,
            positionReceiver,
            filter,
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
    }

    /** Освобождает ресурсы `MediaPlayer` при уничтожении `ViewModel`. */
    override fun onCleared() {
        super.onCleared()
        mediaPlayerRepository.releasePlayer()
    }

    companion object {
        private const val MAX_TRACK_DURATION_MS = 30_000
        private const val PROGRESS_UPDATE_INTERVAL_MS = 500L
        private const val MILLISECONDS_IN_SECOND = 1_000

        private const val ACTION_START_BACKGROUND = "START_BACKGROUND"
        private const val ACTION_REQUEST_POSITION = "REQUEST_POSITION"
        private const val ACTION_UPDATE_POSITION = "UPDATE_POSITION"
        private const val ACTION_STOP = "STOP"
        private const val ACTION_SERVICE_STOPPED = "SERVICE_STOPPED"
        private const val ACTION_UPDATE_PROGRESS = "UPDATE_PROGRESS"

        private const val EXTRA_PREVIEW_URL = "PREVIEW_URL"
        private const val EXTRA_CURRENT_POSITION = "CURRENT_POSITION"
        private const val EXTRA_IS_PLAYING = "IS_PLAYING"
        private const val EXTRA_DURATION = "DURATION"
    }
}