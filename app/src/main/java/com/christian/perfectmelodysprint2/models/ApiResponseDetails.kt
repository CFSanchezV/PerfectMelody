package com.christian.perfectmelodysprint2.models

import com.google.gson.annotations.SerializedName

data class ApiResponseDetails (
    @SerializedName("status_code")
    var status_code: Int,
    @SerializedName("body")
    var songResults: List<Song>
) {

}