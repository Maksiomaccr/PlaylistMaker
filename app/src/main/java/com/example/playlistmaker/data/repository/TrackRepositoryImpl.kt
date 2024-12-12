package com.example.playlistmaker.data.repository

import com.example.playlistmaker.data.network.NetworkClient
import com.example.playlistmaker.data.dto.TrackResponse
import com.example.playlistmaker.data.dto.TrackSearchRequest
import com.example.playlistmaker.domain.models.Resource
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.api.repository.TrackRepository
import java.text.SimpleDateFormat
import java.util.Locale

class TrackRepositoryImpl(private val networkClient: NetworkClient) : TrackRepository {
    private val dateFormat = SimpleDateFormat("m:ss", Locale.getDefault())
    override fun search(term: String): Resource {
        val response = networkClient.doRequest(TrackSearchRequest(term))
        if (response.resultCode == 200) {
            return Resource(
                (response as TrackResponse).results.map {
                    Track(
                        it.trackId ?: 0,
                        it.trackName ?: "no name",
                        it.artistName ?: "no artist",
                        dateFormat.format(it.trackTimeMillis ?: 0),
                        it.artworkUrl100?.replaceAfterLast('/', "512x512bb.jpg") ?: "null",
                        it.collectionName ?: "-",
                        it.releaseDate?.substringBefore('-') ?: "-",
                        it.primaryGenreName ?: "-",
                        it.country ?: "-",
                        it.previewUrl
                    )
                }, response.resultCode
            )
        } else {
            return Resource(null, response.resultCode)
        }
    }
}