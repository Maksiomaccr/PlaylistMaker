package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.interactor.AudioPlayerInteractor
import com.example.playlistmaker.domain.api.repository.AudioPlayerRepository
import com.example.playlistmaker.domain.entities.PlayState

class AudioPlayerInteractorImpl(private val audioPlayerRepository: AudioPlayerRepository):
    AudioPlayerInteractor {
    override fun prepareAudioPlayer(consumer: () -> Unit) {
        audioPlayerRepository.prepareAudioPlayer(consumer)
    }

    override fun pausePlayer() {
        audioPlayerRepository.pausePlayer()
    }

    override fun playbackControl(consumer: (PlayState) -> Unit) {
        audioPlayerRepository.playbackControl(consumer)
    }

    override fun releasePlayer() {
        audioPlayerRepository.releasePlayer()
    }

    override fun isPlayStatePlaying(): Boolean {
        return audioPlayerRepository.isPlayStatePlaying()
    }

    override fun getCurrentPosition(): String {
        return audioPlayerRepository.getCurrentPosition()
    }
}