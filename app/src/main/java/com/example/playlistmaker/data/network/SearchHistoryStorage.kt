package com.example.playlistmaker.data.network

import com.example.playlistmaker.domain.entities.Track

interface SearchHistoryStorage {

    fun getTracks(): MutableList<Track>

    fun putTracks(tracks: List<Track>)

}