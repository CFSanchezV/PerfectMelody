package com.christian.perfectmelodysprint2.services

import com.christian.perfectmelodysprint2.models.ApiResponseDetails
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface SongService {
    @Multipart
    @POST("/sound/upload")
    fun uploadSong(@Header("X-Perfect-Melody-Token")apiKey : String, @Part audio: MultipartBody.Part?): Call<ApiResponseDetails>

    @POST
    fun getSongList(@Header("X-Perfect-Melody-Token")apiKey : String) : Call<ApiResponseDetails>
}