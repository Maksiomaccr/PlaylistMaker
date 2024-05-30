package com.example.playlistmaker

import android.app.Application
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import androidx.appcompat.app.AppCompatDelegate


const val PLAYLIST_MAKER_PREFERENCES = "playlist_maker_preferences"
const val SWITCHER_THEME_KEY = "switcher_theme_key"
var darkTheme = false
class App: Application() {

    override fun onCreate() {
        super.onCreate()

        var checked: OnSharedPreferenceChangeListener = OnSharedPreferenceChangeListener{sharedPreferences, key ->
            if(key == SWITCHER_THEME_KEY){
                darkTheme = sharedPreferences.getBoolean(SWITCHER_THEME_KEY, false)
            }
        }
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
    fun switchSave(darkThemeEnabled: Boolean){
        val sharedPref = getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, MODE_PRIVATE)
        sharedPref.edit().putBoolean(SWITCHER_THEME_KEY, darkThemeEnabled).apply()
    }

}