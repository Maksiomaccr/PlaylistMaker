package com.example.playlistmaker.domain.entities

data class Resource(
    val track: List<Track>?,
    val responseCode: Int,
)