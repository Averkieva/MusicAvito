package com.example.core.data.model

import com.example.core.domain.model.Track

/**
 * Список треков, используемый в `ChartResponse`
 */
data class TrackList(val data: List<Track>)
