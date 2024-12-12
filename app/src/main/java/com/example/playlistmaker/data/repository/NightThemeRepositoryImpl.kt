package com.example.playlistmaker.data.repository

import com.example.playlistmaker.data.storage.NightThemeStorage
import com.example.playlistmaker.data.dto.NightThemeDto
import com.example.playlistmaker.domain.api.repository.NightThemeRepository
import com.example.playlistmaker.domain.models.NightTheme

class NightThemeRepositoryImpl(private val nightThemeStorage: NightThemeStorage):
    NightThemeRepository {

    override fun saveNightMode(nightTheme: NightTheme) {
        val nightThemeDto = NightThemeDto(nightTheme.isNight)
        nightThemeStorage.save(nightThemeDto)
    }

    override fun getNightMode(): NightTheme {
        val nightThemeDto = nightThemeStorage.get()
        return NightTheme(nightThemeDto.isNight)
    }
}