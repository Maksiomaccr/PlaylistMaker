package com.example.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistory(private val sharedPref: SharedPreferences) {

    fun readHistory(): MutableList<Track>{
        val trackList: MutableList<Track> = mutableListOf()
        val trackListString = sharedPref.getString(TRACK_HISTORY, null)
        if(trackListString != null){
            trackList.addAll(createFactFromJson(trackListString))
        }
        return trackList
    }

    private fun createFactFromJson(json: String): Collection<Track> {
        val mutableListTutorialType = object : TypeToken<MutableList<Track>>() {}.type
        return Gson().fromJson(json, mutableListTutorialType)
    }
    private fun createJsonFromFact(tracks: MutableList<Track>): String{
        return Gson().toJson(tracks)
    }

    fun save(trackList: MutableList<Track>){
        sharedPref.edit().putString(TRACK_HISTORY, createJsonFromFact(trackList)).apply()
    }
}