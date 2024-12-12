package com.example.playlistmaker.domain.api.repository

import com.example.playlistmaker.domain.models.PlayState

interface AudioPlayerRepository {
    fun prepareAudioPlayer(consumer: () -> Unit)

    fun startPlayer()

    fun pausePlayer()

    fun playbackControl(consumer: (PlayState) -> Unit)

    fun releasePlayer()

    fun isPlayStatePlaying():Boolean

    fun getCurrentPosition(): String
}