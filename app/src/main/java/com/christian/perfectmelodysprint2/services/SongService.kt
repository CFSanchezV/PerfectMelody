package com.christian.perfectmelodysprint2.services

import com.christian.perfectmelodysprint2.models.ApiResponseDetails
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface SongService {
    //audio in MultipartBody
    @Multipart
    @POST(".")
    fun uploadMultipartSong(@Header("X-Perfect-Melody-Token") apiKey: String,
                   @Part audio: MultipartBody.Part)
            : Call<ApiResponseDetails>

    //audio in body
    @POST(".")
    fun uploadSong(@Header("X-Perfect-Melody-Token") apiKey: String,
                   @Body audio: RequestBody)
            : Call<ApiResponseDetails>

    //empty dummy
    @POST(".")
    fun uploadSongEmpty(@Header("X-Perfect-Melody-Token")apiKey : String)
            : Call<ApiResponseDetails>
}