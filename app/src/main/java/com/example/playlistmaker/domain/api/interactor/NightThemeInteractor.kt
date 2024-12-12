package com.example.playlistmaker.domain.api.interactor

import com.example.playlistmaker.domain.models.NightTheme


interface NightThemeInteractor {

    fun saveNightMode(nightMode: NightTheme)

    fun getNightMode(): NightTheme
}