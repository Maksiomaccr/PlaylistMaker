package com.example.playlistmaker

import android.annotation.SuppressLint
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerActivity : AppCompatActivity() {
    private companion object {
        const val STATE_DEFAULT = 0
        const val STATE_PREPARED = 1
        const val STATE_PLAYING = 2
        const val STATE_PAUSED = 3
        const val DELAY = 1000L
        const val PREVIEW_TIME = 30_000L
    }

    private lateinit var btnPlay: ImageButton
    private lateinit var btnPause: ImageButton
    private var mediaPlayer = MediaPlayer()
    private var previewUrl: String? = null

    private lateinit var backBtn: ImageButton
    private lateinit var trackName: TextView
    private lateinit var artistName: TextView
    private lateinit var trackTime: TextView
    private lateinit var trackImage: ImageView
    private lateinit var album: TextView
    private lateinit var year: TextView
    private lateinit var genre: TextView
    private lateinit var country: TextView
    private lateinit var trackDuration: TextView

    private var playerState = STATE_DEFAULT
    private var mainThreadHandler: Handler? = null
    private var elapsedTime: Long = 0L

    private fun startTimer() {
        val startTime = System.currentTimeMillis()

        mainThreadHandler?.post(
            createUpdateTimerTask(startTime, PREVIEW_TIME, elapsedTime)
        )

    }

    private fun createUpdateTimerTask(startTime: Long, duration: Long, elTime: Long): Runnable {
        return object : Runnable {
            override fun run() {
                elapsedTime = System.currentTimeMillis() - startTime + elTime
                if (elapsedTime <= duration) {
                    if (playerState == STATE_PLAYING) {
                        val seconds = elapsedTime / DELAY
                        trackDuration?.text = String.format("%d:%02d", seconds / 60, seconds % 60)
                        mainThreadHandler?.postDelayed(this, DELAY)
                    }
                } else {
                    pausePlayer()
                    resetTimerUI()
                    elapsedTime = 0L
                }
            }
        }
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_player)
        backBtn = findViewById(R.id.back)
        backBtn.setOnClickListener {
            finish()
        }
        mainThreadHandler = Handler(Looper.getMainLooper())
        btnPlay = findViewById(R.id.playButton)
        btnPause = findViewById(R.id.pauseButton)
        trackDuration = findViewById(R.id.time)

        trackName = findViewById(R.id.trackName)
        artistName = findViewById(R.id.artistName)
        trackTime = findViewById(R.id.trackTime)
        trackImage = findViewById(R.id.trackImage)
        album = findViewById(R.id.album)
        year = findViewById(R.id.year)
        genre = findViewById(R.id.genre)
        country = findViewById(R.id.country)


        val arguments = intent.extras
        if (arguments != null) {
            trackName.text = arguments.getString("trackName")
            artistName.text = arguments.getString("artistName")

            val trackTimeMillis = arguments.getInt("trackTimeMillis")
            trackTime.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(trackTimeMillis)


            val collectionName = arguments.getString("collectionName")
            if (collectionName == null) {
                album.visibility = View.GONE
            } else {
                album.visibility = View.VISIBLE
                album.text = collectionName
            }

            val releaseDate = arguments.getString("releaseDate")
            year.text = releaseDate?.substring(0, 4).toString()
            previewUrl = arguments.getString("previewUrl")
            genre.text = arguments.getString("primaryGenreName")
            country.text = arguments.getString("country")


            val artworkUrl = arguments.getString("artworkUrl100")
            fun getCoverArtwork() = artworkUrl?.replaceAfterLast('/', "512x512bb.jpg")

            val radius = resources.getDimensionPixelSize(R.dimen.album_large_image_radius)

            Glide.with(this)
                .load(getCoverArtwork())
                .placeholder(R.drawable.placeholder_playlist)
                .error(R.drawable.placeholder_playlist)
                .centerCrop()
                .transform(RoundedCorners(radius))
                .into(trackImage)
        }
        preparePlayer()
        btnPlay.setOnClickListener {
            if (playerState == STATE_PREPARED || playerState == STATE_PAUSED) {
                startPlayer()
                startTimer()
            }
        }

        btnPause.setOnClickListener {
            if (playerState == STATE_PLAYING) {
                pausePlayer()
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()

    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    private fun resetTimerUI() {
        trackDuration?.text = "0:00"
    }

    private fun preparePlayer() {
        mediaPlayer.setDataSource(previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerState = STATE_PREPARED
            resetTimerUI()
        }
        mediaPlayer.setOnCompletionListener {
            btnPlay.visibility = View.VISIBLE
            playerState = STATE_PREPARED
        }
    }


    private fun startPlayer() {
        mediaPlayer.start()
        btnPlay.visibility = View.GONE
        btnPause.visibility = View.VISIBLE
        playerState = STATE_PLAYING
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        btnPlay.visibility = View.VISIBLE
        btnPause.visibility = View.GONE
        playerState = STATE_PAUSED
    }
}