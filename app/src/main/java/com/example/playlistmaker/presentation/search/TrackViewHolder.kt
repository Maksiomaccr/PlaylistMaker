package com.example.playlistmaker.presentation.search

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.entities.Track
import java.text.SimpleDateFormat
import java.util.Locale

class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val trackName: TextView
    private val artistName: TextView
    private val trackTime: TextView
    private val image: ImageView

    init {
        trackName = itemView.findViewById(R.id.track_name)
        artistName = itemView.findViewById(R.id.track_artist)
        trackTime = itemView.findViewById(R.id.time)
        image = itemView.findViewById(R.id.track_image)
    }

    fun bind(track: Track, onClick: (Track) -> Unit) {
        trackName.text = track.trackName
        artistName.text = track.artistName
        trackTime.text = track.trackTimeMillis

        artistName.requestLayout()
        Glide
            .with(itemView.context)
            .load(track.artworkUrl100)
            .placeholder(R.drawable.placeholder_playlist)
            .error(R.drawable.placeholder_playlist)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .fitCenter()
            .transform(RoundedCorners(10))
            .into(image)
        itemView.setOnClickListener {
            onClick.invoke(track)
        }
    }
}