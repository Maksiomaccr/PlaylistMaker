package com.example.playlistmaker.domain.api.repository

import com.example.playlistmaker.domain.entities.NightTheme

interface NightThemeRepository {

    fun saveNightMode(nightMode: NightTheme)

    fun getNightMode(): NightTheme
}