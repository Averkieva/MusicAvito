package com.example.core.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.core.databinding.ItemTrackBinding
import com.example.core.domain.model.Track

/**
 * Абстрактный базовый адаптер, от которого наследуются `ApiTrackAdapter` и `DownloadedTracksAdapter`.
 * Предоставляет общую логику для работы со списком треков в `RecyclerView`.
 *
 * @param VH Тип ViewHolder, который будет использоваться в адаптере.
 * @param onTrackClick Лямбда-обработчик клика по треку, передающий `trackId`.
 */
abstract class BaseTrackAdapter<VH : RecyclerView.ViewHolder>(
    val onTrackClick: (String) -> Unit
) : RecyclerView.Adapter<VH>() {

    protected var tracks: List<Track> = emptyList()

    override fun getItemCount(): Int = tracks.size

    /**
     * Обновляет список треков, используя `DiffUtil`, чтобы избежать лишних перерисовок элементов.
     *
     * @param newTracks Новый список треков для обновления.
     */
    fun updateTracks(newTracks: List<Track>) {
        val diffCallback = TrackDiffCallback(tracks, newTracks)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        tracks = newTracks
        diffResult.dispatchUpdatesTo(this)
    }

    /**
     * Создаёт `ItemTrackBinding` для привязки данных к элементу списка.
     *
     * @param parent Родительский `ViewGroup`, к которому будет прикреплен элемент списка.
     * @return `ItemTrackBinding` для элемента списка треков.
     */
    protected fun createTrackBinding(parent: ViewGroup): ItemTrackBinding {
        return ItemTrackBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    }
}
