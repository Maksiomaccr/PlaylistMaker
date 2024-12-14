package com.example.playlistmaker.domain.api.interactor

import com.example.playlistmaker.domain.entities.Track

interface SearchHistoryInteractor {

    fun getTrackHistory():List<Track>

    fun addToTrackHistory(track: Track)

    fun clearTrackHistory()
}