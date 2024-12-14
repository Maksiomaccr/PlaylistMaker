package com.example.playlistmaker.data.network

import com.example.playlistmaker.data.dto.Response
import com.example.playlistmaker.data.dto.TrackSearchRequest
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitNetworkClient : NetworkClient {

    private val itunesBaseUrl = "https://itunes.apple.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(itunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val iTunesApi = retrofit.create(ITunesAPI::class.java)

    override fun doRequest(dto: Any): Response {
        try {
            if (dto is TrackSearchRequest) {
                val resp = iTunesApi.search(dto.expression).execute()

                val body = resp.body() ?: Response()

                return body.apply { resultCode = 200 }
            } else {
                return Response().apply { resultCode = 400 }
            }
        } catch (e: Exception) {
            return Response().apply { resultCode = 404 }
        }
    }
}
