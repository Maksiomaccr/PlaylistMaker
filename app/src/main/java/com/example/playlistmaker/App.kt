package com.example.playlistmaker

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.creator.Creator

class App: Application() {

    companion object{
        var darkTheme: Boolean = false
    }

    override fun onCreate() {
        super.onCreate()
        Creator.initAppContext(applicationContext)
        darkTheme = Creator.provideNightThemeInteractor().getNightMode().isNight
        switchTheme(darkTheme)
    }
    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }



}