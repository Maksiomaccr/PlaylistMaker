package com.example.playlistmaker.presentation.settings

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.playlistmaker.App
import com.example.playlistmaker.App.Companion.darkTheme
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivitySettingsBinding
import com.example.playlistmaker.domain.entities.NightTheme

class SettingsActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId", "UseSwitchCompatOrMaterialCode")

    private lateinit var binding: ActivitySettingsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.themeSwitch.isChecked = darkTheme

        bindingThemeSwitcher()
        bindingShare()
        bindingSupport()
        bindingForward()


        binding.back.setOnClickListener {
            finish()
        }

    }

    private fun bindingThemeSwitcher() {
        binding.themeSwitch.setOnCheckedChangeListener { switcher, checked ->
            (applicationContext as App).switchTheme(checked)
            Creator.provideNightThemeInteractor().saveNightMode(NightTheme(checked))
        }
    }

    private fun bindingShare() {
        binding.share.setOnClickListener {
            Intent(Intent.ACTION_SENDTO).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, getString(R.string.url))
                startActivity(Intent.createChooser(this, "Share"))
            }
        }
    }

    private fun bindingSupport() {
        binding.support.setOnClickListener {
            Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf("maxcraft.vjugov@ya.ru"))
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.subject))
                putExtra(Intent.EXTRA_TEXT, getString(R.string.message))
                startActivity(this)
            }
        }
    }

    private fun bindingForward() {
        binding.forward.setOnClickListener {
            Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(getString(R.string.url_offer))
                startActivity(this)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

    }
}
