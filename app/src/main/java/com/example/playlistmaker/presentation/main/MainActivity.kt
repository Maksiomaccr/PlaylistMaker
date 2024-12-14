package com.example.playlistmaker.presentation.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import com.example.playlistmaker.presentation.media.MediaActivity
import com.example.playlistmaker.R
import com.example.playlistmaker.presentation.search.SearchActivity
import com.example.playlistmaker.presentation.settings.SettingsActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val settings = findViewById<Button>(R.id.settings_button)
        val media = findViewById<Button>(R.id.media_button)
        val search = findViewById<Button>(R.id.search_button)


        settings.setOnClickListener{
            val settingsIntent = Intent(this, SettingsActivity::class.java)
            startActivity(settingsIntent)
        }
        media.setOnClickListener{
            val mediaIntent = Intent(this, MediaActivity::class.java)
            startActivity(mediaIntent)
        }
        search.setOnClickListener{
            val searchIntent = Intent(this, SearchActivity::class.java)
            startActivity(searchIntent)
        }
    }
}