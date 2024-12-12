package com.example.playlistmaker

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.domain.models.PlayState
import com.example.playlistmaker.domain.models.Track

class AudioPlayerViewModel(val track: Track) : ViewModel() {
    companion object {
        const val TIMER_REFRESH_DELAY = 300L
        const val CURRENT_TIME_ZERO = "0:00"
    }

    val interactor = Creator.provideAudioPlayerInteractor()
    private val handler = Handler(Looper.getMainLooper())

    private val _currentTime = MutableLiveData<String>()
    private val _playButtonImage = MutableLiveData<Int>()

    val currentTime: LiveData<String> = _currentTime
    val playButtonImage: LiveData<Int> = _playButtonImage

    init {
        interactor.prepareAudioPlayer {
            _playButtonImage.value = R.drawable.play_btn
            _currentTime.value = CURRENT_TIME_ZERO
        }
    }

    override fun onCleared() {
        super.onCleared()
        interactor.releasePlayer()
    }

    private val trackTimerRunnable = object : Runnable {
        override fun run() {
            if (interactor.isPlayStatePlaying()) {
                val currentTimer = interactor.getCurrentPosition()
                _currentTime.value = currentTimer
                handler.postDelayed(this, TIMER_REFRESH_DELAY)
            }
        }
    }

    fun playerButtonStateChanger(playState: PlayState) {
        when (playState) {
            PlayState.STATE_PLAYING -> {
                _playButtonImage.value = R.drawable.pause_btn
                trackTimerRunnable.run()
            }

            PlayState.STATE_PREPARED, PlayState.STATE_PAUSED -> {
                _playButtonImage.value = R.drawable.play_btn
                handler.removeCallbacks(trackTimerRunnable)

            }
            PlayState.STATE_DEFAULT -> {}
        }
    }



}