package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.interactor.NightThemeInteractor
import com.example.playlistmaker.domain.api.repository.NightThemeRepository
import com.example.playlistmaker.domain.entities.NightTheme

class NightThemeInteractorImpl(private val repository: NightThemeRepository): NightThemeInteractor {
    override fun saveNightMode(nightMode: NightTheme) {
        repository.saveNightMode(nightMode)
    }

    override fun getNightMode(): NightTheme {
        return repository.getNightMode()
    }
}
