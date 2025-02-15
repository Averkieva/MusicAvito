package com.example.feature_playback_tracks.data.repository

import android.media.MediaPlayer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.feature_playback_tracks.domain.repository.MediaPlayerRepository
import com.example.feature_playback_tracks.ui.player.viewmodel.SharedTrackViewModel
import javax.inject.Inject

class MediaPlayerRepositoryImpl @Inject constructor(
    private val sharedViewModel: SharedTrackViewModel
) : MediaPlayerRepository {

    private var mediaPlayer: MediaPlayer? = null
    private val _isPlaying = MutableLiveData<Boolean>()
    override val isPlaying: LiveData<Boolean> get() = _isPlaying

    override fun prepareMediaPlayer(previewUrl: String, autoPlay: Boolean) {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer().apply {
            setDataSource(previewUrl)
            prepareAsync()
            setOnPreparedListener {
                _isPlaying.postValue(autoPlay)
                if (autoPlay) start()
            }
            setOnCompletionListener {
                _isPlaying.postValue(false)
                val nextTrack = sharedViewModel.getNextTrack()
                if (nextTrack != null) {
                    sharedViewModel.setCurrentTrack(nextTrack.id)
                    prepareMediaPlayer(nextTrack.preview, true)
                }
            }
        }
    }

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

    override fun skipToNext() {
        mediaPlayer?.seekTo((mediaPlayer?.currentPosition ?: INITIAL_POSITION_MS) + SEEK_FORWARD_MS)
    }

    override fun skipToPrevious() {
        mediaPlayer?.seekTo((mediaPlayer?.currentPosition ?: INITIAL_POSITION_MS) - SEEK_BACKWARD_MS)
    }

    override fun getCurrentPosition(): Int {
        return mediaPlayer?.currentPosition ?: INITIAL_POSITION_MS
    }

    override fun seekTo(position: Int) {
        mediaPlayer?.seekTo(position)
    }

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