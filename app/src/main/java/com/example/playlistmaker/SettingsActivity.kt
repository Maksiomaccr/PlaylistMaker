package com.example.playlistmaker

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton

class SettingsActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val back = findViewById<ImageButton>(R.id.back)
        val share = findViewById<Button>(R.id.share)
        val support = findViewById<Button>(R.id.support)
        val forward = findViewById<Button>(R.id.forward)
        back.setOnClickListener{
            finish()
        }
        share.setOnClickListener{

        val shareIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf("yourEmail@ya.ru"))
            putExtra(Intent.EXTRA_TEXT, getString(R.string.url))
            startActivity(this)
             }

        }

        support.setOnClickListener{
            val supportIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf("maxcraft.vjugov@ya.ru"))
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.subject))
                putExtra(Intent.EXTRA_TEXT, getString(R.string.message))
                startActivity(this)
            }

        }
        forward.setOnClickListener{
            val forwardIntent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(getString(R.string.url_offer))
                startActivity(this)
            }

        }
    }
}