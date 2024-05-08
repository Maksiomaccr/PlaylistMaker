package com.example.playlistmaker

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class TrackViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView){
    
    private val trackName: TextView
    private val artistName: TextView
    private val trackTime: TextView

init {
    trackName = itemView.findViewById(R.id.track_name)
    artistName = itemView.findViewById(R.id.track_artist)
    trackTime = itemView.findViewById(R.id.track_time)

}
    fun bind(track: Track){
        trackName.text = track.trackName
        artistName.text = track.artistName
        trackTime.text = track.trackTime


        val image = itemView.findViewById<ImageView>(R.id.track_image)
        Glide
            .with(itemView.context)
            .load(track.artworkUrl100)
            .placeholder(R.drawable.placeholder)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .fitCenter()
            .transform(RoundedCorners(5))
            .into(image)
    }
}