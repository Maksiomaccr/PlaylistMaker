package com.example.playlistmaker.domain.api.repository

import com.example.playlistmaker.domain.entities.Track

interface SearchHistoryRepository {
    fun getTracksFromStorage(): MutableList<Track>

    fun putTracksToStorage(tracks: List<Track>)

}
