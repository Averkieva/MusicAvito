package com.example.core.data.model

import com.example.core.domain.model.Track

/**
 * Ответ API для поиска треков
 */
data class SearchResponse(val data: List<Track>)