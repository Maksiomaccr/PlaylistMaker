package com.example.playlistmaker.domain.api.repository

import com.example.playlistmaker.domain.entities.Resource

interface TrackRepository {
    fun search(term: String): Resource
}