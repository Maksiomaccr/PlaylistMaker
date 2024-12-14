package com.example.playlistmaker.data.storage

import com.example.playlistmaker.data.dto.NightThemeDto

interface NightThemeStorage {
    fun save(isNight: NightThemeDto)

    fun get(): NightThemeDto
}
