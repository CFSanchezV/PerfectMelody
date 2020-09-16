package com.christian.perfectmelodysprint2.activities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.christian.perfectmelodysprint2.AudioManager
import com.christian.perfectmelodysprint2.R
import com.christian.perfectmelodysprint2.adapters.OnSongClickListener
import com.christian.perfectmelodysprint2.adapters.SongAdapter
import com.christian.perfectmelodysprint2.models.ApiResponseDetails
import com.christian.perfectmelodysprint2.models.Song
import com.christian.perfectmelodysprint2.services.SongService
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_song_list.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

class SongListActivity : AppCompatActivity(), OnSongClickListener {
    private val TAG = "SongListActivity"

    private lateinit var audioManager: AudioManager

    lateinit var songs : List<Song>
    lateinit var songAdapter: SongAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_song_list)

        emptyUploadFile()
    }

    override fun onItemClicked(song: Song, btn: View) {
        when(btn.id) {
            R.id.youtubeBtn -> {
                val youtubeIntent = Intent(Intent.ACTION_VIEW)
                youtubeIntent.data = Uri.parse("https://www.youtube.com/results?search_query="+ song.name)
                startActivity(youtubeIntent)
            }
            R.id.spotifyBtn -> {
                val spotifyIntent = Intent(Intent.ACTION_VIEW)
                spotifyIntent.data = Uri.parse("https://open.spotify.com/search/"+ song.name)
                startActivity(spotifyIntent)
            }
            R.id.soundCloudBtn -> {
                val soundcloudIntent = Intent(Intent.ACTION_VIEW)
                soundcloudIntent.data = Uri.parse("https://soundcloud.com/search?q="+ song.name)
                startActivity(soundcloudIntent)
            }
        }
    }

    //NETWORKING SECTION
    private fun emptyUploadFile() {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://18.191.176.24/api/ranking")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(SongService::class.java)

        val emptyRequest = service.getSongList("xma1lhVnlBe9aruYvZY4W8q1ruSmpshmMwLTYFohB9mrOlkCdI")

        emptyRequest.enqueue(object : Callback<ApiResponseDetails> {
            override fun onFailure(call: Call<ApiResponseDetails>, t: Throwable) {
                //Log error Message
                Log.d(TAG, "Error: $t")
            }

            override fun onResponse(call: Call<ApiResponseDetails>, response: Response<ApiResponseDetails>) {
                //Load songList on recyclerView
                if(response.isSuccessful) {
                    songs = response.body()!!.songResults
                    songAdapter = SongAdapter(songs, this@SongListActivity)
                    rvSongList.adapter = songAdapter
                    rvSongList.layoutManager = LinearLayoutManager(this@SongListActivity)
                }
                else {
                    Log.d(TAG, "No se envío el tarareo")
                }
            }
        })
    }

    private fun uploadFile() {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://18.191.176.24/api/ranking")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service = retrofit.create(SongService::class.java)

        //build body
        var body: MultipartBody.Part? = null
        val fileUri = audioManager.filePathForId(recImgButton.tag.toString())
        val audioFile : File? = File(fileUri)
        val inputStream = contentResolver.openInputStream(Uri.fromFile(audioFile))
        val requestFile = RequestBody.create(
            MediaType.parse("audio/MPEG_4"),
            inputStream!!.readBytes()
        )
        if (audioFile != null) {
            body = MultipartBody.Part.createFormData("audio", audioFile.name, requestFile)
            Log.d("Name of file sent", audioFile.name)
        }

        //call retrofit to make the request
        service.uploadSong("xma1lhVnlBe9aruYvZY4W8q1ruSmpshmMwLTYFohB9mrOlkCdI", body)
    }
}
