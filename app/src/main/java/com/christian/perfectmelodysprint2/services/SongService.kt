package com.christian.perfectmelodysprint2.services

import com.christian.perfectmelodysprint2.models.ApiResponseDetails
import retrofit2.Call
import retrofit2.http.Header
import retrofit2.http.POST

interface SongService {
    @POST("song?")
    fun getSongList(@Header("X-Perfect-Melody-Token")apiKey : String, @Header("Content-Type:application/json")contentType : String) : Call<ApiResponseDetails>
}