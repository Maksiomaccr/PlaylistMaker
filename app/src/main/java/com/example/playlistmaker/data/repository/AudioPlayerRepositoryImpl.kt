package com.example.playlistmaker.data.repository

import com.example.playlistmaker.domain.api.repository.AudioPlayerRepository
import com.example.playlistmaker.domain.entities.PlayState
import com.example.playlistmaker.domain.entities.Track
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerRepositoryImpl(private val track : Track) : AudioPlayerRepository {
    private val audioPlayer = android.media.MediaPlayer()
    private var playState = PlayState.STATE_DEFAULT

    private val dateFormat = SimpleDateFormat("m:ss", Locale.getDefault())

    override fun prepareAudioPlayer(consumer: () -> Unit) {
        audioPlayer.setDataSource(track.previewUrl)
        audioPlayer.prepareAsync()
        audioPlayer.setOnPreparedListener {
            playState = PlayState.STATE_PREPARED
        }
        audioPlayer.setOnCompletionListener {
            playState = PlayState.STATE_PREPARED
            consumer.invoke()
        }
    }

    override fun startPlayer() {
        audioPlayer.start()
        playState = PlayState.STATE_PLAYING
    }

    override fun pausePlayer() {
        audioPlayer.pause()
        playState = PlayState.STATE_PAUSED
    }

    override fun playbackControl(consumer: (PlayState) -> Unit) {
        when (playState) {
            PlayState.STATE_PLAYING -> {
                pausePlayer()
            }

            PlayState.STATE_PREPARED, PlayState.STATE_PAUSED -> {
                startPlayer()
            }

            PlayState.STATE_DEFAULT -> {

            }
        }
        consumer.invoke(playState)
    }

    override fun releasePlayer() {
        audioPlayer.release()
    }

    override fun isPlayStatePlaying(): Boolean {
        return playState == PlayState.STATE_PLAYING
    }

    override fun getCurrentPosition(): String {
        return dateFormat.format(audioPlayer.currentPosition)
    }
}
