package com.example.playlistmaker

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate

const val DARK_THEME = "is_dark_theme"
const val PREFERENCES_THEME = "theme_pref"

class App: Application() {
    var darkTheme = false
    private lateinit var checked: SharedPreferences.OnSharedPreferenceChangeListener
    override fun onCreate() {
        super.onCreate()

        val sharedPref = getSharedPreferences(PREFERENCES_THEME, MODE_PRIVATE)
        var stringFromSharedPrefs = sharedPref.getBoolean(DARK_THEME, false)
        darkTheme = when(stringFromSharedPrefs){
            false -> false
            true -> true
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
        val sharedPref: SharedPreferences = getSharedPreferences(PREFERENCES_THEME, MODE_PRIVATE)
        sharedPref.edit().putBoolean(DARK_THEME, darkThemeEnabled).apply()
    }


}