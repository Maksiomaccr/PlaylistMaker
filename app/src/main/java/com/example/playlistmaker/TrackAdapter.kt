package com.example.playlistmaker

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class TrackAdapter(var tracks: List<Track>, private val searchActivity: SearchActivity?): RecyclerView.Adapter<TrackViewHolder> (){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.tracks_item, parent, false)
        return TrackViewHolder(view)
    }

    override fun getItemCount(): Int {
return tracks.size
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {

        holder.bind(tracks[position])
        holder.itemView.setOnClickListener{
            searchActivity?.updateTrackHistory(position)
        }
    }
}