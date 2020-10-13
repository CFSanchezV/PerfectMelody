package com.christian.perfectmelodysprint2.models
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Entity(
    tableName = "songs"
)
data class Song (
    @SerializedName("name")
    val name : String,
    @SerializedName("artist")
    val artist : String,
    @SerializedName("confidence")
    val confidence : Float,

    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    val Id : Int = 0
): Serializable {
}