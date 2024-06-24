package com.example.playlistmaker

import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerActivity: AppCompatActivity() {
    private lateinit var backBtn: ImageButton
    private lateinit var trackName: TextView
    private lateinit var artistName: TextView
    private lateinit var trackTime: TextView
    private lateinit var trackImage: ImageView
    private lateinit var album: TextView
    private lateinit var year: TextView
    private lateinit var genre: TextView
    private lateinit var country: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audioplayer)

        backBtn = findViewById(R.id.back)
        backBtn.setOnClickListener{
            finish()
        }

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

            genre.text = arguments.getString("primaryGenreName")
            country.text = arguments.getString("country")

            val artworkUrl = arguments.getString("artworkUrl100")
            fun getCoverArtwork() = artworkUrl?.replaceAfterLast('/',"512x512bb.jpg")

            val radius = resources.getDimensionPixelSize(R.dimen.album_large_image_radius)

            Glide.with(this)
                .load(getCoverArtwork())
                .placeholder(R.drawable.placeholder_playlist)
                .error(R.drawable.placeholder_playlist)
                .centerCrop()
                .transform(RoundedCorners(radius))
                .into(trackImage)
        }

    }
}