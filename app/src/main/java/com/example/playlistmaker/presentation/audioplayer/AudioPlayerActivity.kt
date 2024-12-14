package com.example.playlistmaker.presentation.audioplayer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.data.repository.AudioPlayerRepositoryImpl
import com.example.playlistmaker.databinding.ActivityAudioPlayerBinding

class AudioPlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAudioPlayerBinding
    private lateinit var viewModel: AudioPlayerViewModel
    private lateinit var media: AudioPlayerRepositoryImpl

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAudioPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(
            this,
            TrackManager(intent.extras?.getString("track")!!)
        )[AudioPlayerViewModel::class.java]
        bindTrack()
        addObservers()

        binding.back.setOnClickListener {
            finish()
        }
        binding.playButton.setOnClickListener {
            viewModel.interactor.playbackControl {
                viewModel.playerButtonStateChanger(it)
            }
        }
    }

    private fun addObservers() {
        viewModel.currentTime.observe(this@AudioPlayerActivity) {
            binding.time.text = it
        }

        viewModel.playButtonImage.observe(this@AudioPlayerActivity) {
            binding.playButton.setImageResource(it)
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
    }

    private fun bindTrack() {
        val radius = resources.getDimensionPixelSize(R.dimen.album_large_image_radius)
        binding.trackName.text = viewModel.track.trackName
        binding.artistName.text = viewModel.track.artistName
        binding.trackTime.text = viewModel.track.trackTimeMillis

        if (viewModel.track.collectionName.endsWith(" - Single") || viewModel.track.collectionName == "-") {
            binding.albumStr.isVisible = false
        } else {
            binding.album.text = viewModel.track.collectionName
        }
        if (viewModel.track.releaseDate == "-") {
            binding.yearStr.isVisible = false
        } else {
            binding.year.text = viewModel.track.releaseDate
        }
        binding.genre.text = viewModel.track.primaryGenreName
        binding.country.text = viewModel.track.country
        Glide.with(this)
            .load(viewModel.track.artworkUrl100)
            .placeholder(R.drawable.placeholder_playlist)
            .error(R.drawable.placeholder_playlist)
            .centerCrop()
            .transform(RoundedCorners(radius))
            .into(binding.trackImage)
    }

    override fun onPause() {
        super.onPause()
        viewModel.interactor.pausePlayer()
    }
}