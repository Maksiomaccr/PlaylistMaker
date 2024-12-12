package com.example.playlistmaker.creator

import android.content.Context
import com.example.playlistmaker.data.storage.SharedPrefNightThemeStorage
import com.example.playlistmaker.data.storage.SharedPrefsSearchHistoryStorage
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.data.repository.AudioPlayerRepositoryImpl
import com.example.playlistmaker.data.repository.TrackRepositoryImpl
import com.example.playlistmaker.data.repository.NightThemeRepositoryImpl
import com.example.playlistmaker.data.repository.SearchHistoryRepositoryImpl
import com.example.playlistmaker.domain.api.repository.TrackRepository
import com.example.playlistmaker.domain.api.repository.AudioPlayerRepository
import com.example.playlistmaker.domain.api.repository.NightThemeRepository
import com.example.playlistmaker.domain.api.repository.SearchHistoryRepository
import com.example.playlistmaker.domain.api.interactor.AudioPlayerInteractor
import com.example.playlistmaker.domain.api.interactor.NightThemeInteractor
import com.example.playlistmaker.domain.api.interactor.SearchHistoryInteractor
import com.example.playlistmaker.domain.api.interactor.TrackInteractor
import com.example.playlistmaker.domain.impl.AudioPlayerInteractorImpl
import com.example.playlistmaker.domain.impl.NightThemeInteractorImpl
import com.example.playlistmaker.domain.impl.SearchHistoryInteractorImpl
import com.example.playlistmaker.domain.impl.TrackInteractorImpl
import com.example.playlistmaker.domain.models.Track

object Creator {

    private lateinit var appContext: Context
    private lateinit var trackToOpenPlayerActivity: Track

    fun initAppContext(context: Context) {
        appContext = context
    }
    fun initTrack(newTrack: Track) {
        trackToOpenPlayerActivity = newTrack
    }
    private fun getSearchHistoryRepository(): SearchHistoryRepository {
        return SearchHistoryRepositoryImpl(SharedPrefsSearchHistoryStorage(appContext))
    }
    fun provideSearchHistoryInteractor(): SearchHistoryInteractor {
        return SearchHistoryInteractorImpl(getSearchHistoryRepository())
    }
    private fun getTrackRepository(): TrackRepository {
        return TrackRepositoryImpl(RetrofitNetworkClient())
    }
    fun provideTrackInteractor(): TrackInteractor {
        return TrackInteractorImpl(getTrackRepository())
    }
    private fun getNightThemeRepository(): NightThemeRepository {
        return NightThemeRepositoryImpl(SharedPrefNightThemeStorage(appContext))
    }
    fun provideNightThemeInteractor(): NightThemeInteractor {
        return NightThemeInteractorImpl(getNightThemeRepository())
    }
    private fun getAudioPlayer(): AudioPlayerRepository {
        return AudioPlayerRepositoryImpl(trackToOpenPlayerActivity)
    }

    fun provideAudioPlayerInteractor(): AudioPlayerInteractor {
        return AudioPlayerInteractorImpl(getAudioPlayer())
    }

}