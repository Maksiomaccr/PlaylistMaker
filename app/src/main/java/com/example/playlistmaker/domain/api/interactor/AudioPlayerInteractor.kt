package com.example.playlistmaker.domain.api.interactor

import com.example.playlistmaker.domain.models.PlayState

interface AudioPlayerInteractor {

    fun prepareAudioPlayer(consumer: () -> Unit)

    fun pausePlayer()

    fun playbackControl(consumer: (PlayState) -> Unit)

    fun releasePlayer()

    fun isPlayStatePlaying():Boolean

    fun getCurrentPosition(): String
}