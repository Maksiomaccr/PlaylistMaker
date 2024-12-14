package com.example.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.creator.Creator

const val DARK_THEME = "is_dark_theme"
const val PREFERENCES_THEME = "theme_pref"

class App : Application() {

    companion object {
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
