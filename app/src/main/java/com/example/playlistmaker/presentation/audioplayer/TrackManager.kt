package com.example.playlistmaker.presentation.audioplayer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.domain.entities.Track
import com.google.gson.Gson

class TrackManager(val string: String) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AudioPlayerViewModel(Gson().fromJson(string, Track::class.java)) as T
    }
}
