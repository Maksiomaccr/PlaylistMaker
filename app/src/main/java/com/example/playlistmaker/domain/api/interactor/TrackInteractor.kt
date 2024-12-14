package com.example.playlistmaker.domain.api.interactor

import com.example.playlistmaker.domain.entities.Resource

interface TrackInteractor {
    fun searchTrack(term: String, consumer: TrackConsumer)

    interface TrackConsumer {
        fun consume(foundTracksResource: Resource)
    }
}
