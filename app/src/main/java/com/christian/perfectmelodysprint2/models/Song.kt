package com.christian.perfectmelodysprint2.models
import com.google.gson.annotations.SerializedName

data class Song (
    @SerializedName("name")
    val name : String,
    @SerializedName("artist")
    val artist : String,
    @SerializedName("confidence")
    val confidence : Int
) {
}