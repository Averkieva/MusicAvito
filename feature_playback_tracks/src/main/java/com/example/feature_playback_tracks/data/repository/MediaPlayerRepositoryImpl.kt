package com.example.feature_playback_tracks.data.repository

import android.media.MediaPlayer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.core.ui.viewmodel.SharedTrackViewModel
import com.example.feature_playback_tracks.domain.repository.MediaPlayerRepository
import javax.inject.Inject

/**
 * Реализация `MediaPlayerRepository`, которая управляет воспроизведением треков с помощью `MediaPlayer`.
 * Позволяет запускать, ставить на паузу, переключать треки и управлять их положением.
 *
 * @param sharedViewModel Общий `SharedTrackViewModel`, используемый для переключения треков.
 */
class MediaPlayerRepositoryImpl @Inject constructor(
    private val sharedViewModel: SharedTrackViewModel
) : MediaPlayerRepository {

    /**
     * Экземпляр `MediaPlayer` для воспроизведения аудиофайлов.
     */
    private var mediaPlayer: MediaPlayer? = null

    private val _isPlaying = MutableLiveData<Boolean>()
    override val isPlaying: LiveData<Boolean> get() = _isPlaying

    /**
     * Подготавливает `MediaPlayer` для воспроизведения трека.
     * Если `autoPlay == true`, то после загрузки аудио начнется воспроизведение.
     *
     * @param previewUrl URL аудиофайла.
     * @param autoPlay Флаг автоматического старта воспроизведения.
     */
    override fun prepareMediaPlayer(previewUrl: String, autoPlay: Boolean) {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer().apply {
            setDataSource(previewUrl) // Устанавливает источник аудио
            prepareAsync() // Подготавливает плеер асинхронно
            setOnPreparedListener {
                _isPlaying.postValue(autoPlay) // Обновляет состояние воспроизведения
                if (autoPlay) start()
            }
            setOnCompletionListener {
                _isPlaying.postValue(false) // Устанавливает флаг, что воспроизведение завершилось
                val nextTrack = sharedViewModel.getNextTrack() // Получает следующий трек
                if (nextTrack != null) {
                    sharedViewModel.setCurrentTrack(nextTrack.id)
                    prepareMediaPlayer(nextTrack.preview, true) // Автоматически переключает трек
                }
            }
        }
    }

    /**
     * Переключает состояние плеера: воспроизведение или пауза.
     */
    override fun togglePlayPause() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.pause()
                _isPlaying.postValue(false)
            } else {
                it.start()
                _isPlaying.postValue(true)
            }
        }
    }

    /**
     * Перематывает воспроизведение вперед на 10 секунд.
     */
    override fun skipToNext() {
        mediaPlayer?.seekTo((mediaPlayer?.currentPosition ?: INITIAL_POSITION_MS) + SEEK_FORWARD_MS)
    }

    /**
     * Перематывает воспроизведение назад на 10 секунд.
     */
    override fun skipToPrevious() {
        mediaPlayer?.seekTo(
            (mediaPlayer?.currentPosition ?: INITIAL_POSITION_MS) - SEEK_BACKWARD_MS
        )
    }

    /**
     * Возвращает текущую позицию воспроизведения в миллисекундах.
     *
     * @return Позиция в миллисекундах.
     */
    override fun getCurrentPosition(): Int {
        return mediaPlayer?.currentPosition ?: INITIAL_POSITION_MS
    }

    /**
     * Устанавливает новую позицию воспроизведения.
     *
     * @param position Позиция в миллисекундах.
     */
    override fun seekTo(position: Int) {
        mediaPlayer?.seekTo(position)
    }

    /**
     * Освобождает ресурсы `MediaPlayer` при завершении работы.
     */
    override fun releasePlayer() {
        mediaPlayer?.release()
        mediaPlayer = null
    }

    companion object {
        private const val SEEK_FORWARD_MS = 10_000
        private const val SEEK_BACKWARD_MS = 10_000
        private const val INITIAL_POSITION_MS = 0
    }
}