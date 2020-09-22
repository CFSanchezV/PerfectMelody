package com.christian.perfectmelodysprint2.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.christian.perfectmelodysprint2.AudioManager
import com.christian.perfectmelodysprint2.BuildConfig
import com.christian.perfectmelodysprint2.R
import com.christian.perfectmelodysprint2.adapters.OnSongClickListener
import com.christian.perfectmelodysprint2.adapters.SongAdapter
import com.christian.perfectmelodysprint2.models.ApiResponseDetails
import com.christian.perfectmelodysprint2.models.Song
import com.christian.perfectmelodysprint2.services.SongService
import kotlinx.android.synthetic.main.activity_song_list.*
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

class SongListActivity : AppCompatActivity(), OnSongClickListener {
    private val TAG = "SongListActivity"
    private val ApiKey = BuildConfig.PERFECTMELODY_API_KEY

    lateinit var songs : List<Song>
    lateinit var songAdapter: SongAdapter

    private lateinit var audioManager: AudioManager

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_song_list)
        //SupportBar
        setSupportActionBar(toolbar_list)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        audioManager = AudioManager(this)

        //emptyUploadFile()  //Working OK
        uploadFile()  //Needs Testing
        //Toast.makeText(this, "api key es: $ApiKey",Toast.LENGTH_SHORT).show(); //Test ApiKey value
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
            .baseUrl("http://18.191.176.24/api/ranking/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service = retrofit.create(SongService::class.java)

        val emptyRequest = service.uploadSongEmpty(apiKey = ApiKey)

        emptyRequest.enqueue(object : Callback<ApiResponseDetails> {
            override fun onFailure(call: Call<ApiResponseDetails>, t: Throwable) {
                //Log.d(TAG, "Error: $t")
                Toast.makeText(this@SongListActivity, "Error: $t",Toast.LENGTH_SHORT).show();
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
                    //Log.d(TAG, "No se envío el tarareo")
                    Toast.makeText(this@SongListActivity, "No se envió el tarareo",Toast.LENGTH_SHORT).show();
                }
            }
        })
    }

    private fun uploadFile() {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://18.191.176.24/api/ranking/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val postService = retrofit.create(SongService::class.java)

        val filePath  = audioManager.filePathForId("audioFile")
        val audioFile : File? = File(filePath)

        /*
        //build body
        var body: MultipartBody.Part? = null
        val inputStream = contentResolver.openInputStream(Uri.fromFile(audioFile))
        //+ inputStream!!.readBytes() in create

        if (audioFile != null) {
            body = MultipartBody.Part.createFormData("audio", audioFile.name, requestBody)
            Toast.makeText(this@SongListActivity, "Name of file sent: ${audioFile.name}", Toast.LENGTH_SHORT).show();
        }
        */

        val requestBody = RequestBody.create(MediaType.parse("audio/*"), audioFile!!)
        val myRequest = postService.uploadSong(ApiKey, requestBody)

        myRequest.enqueue(object : Callback<ApiResponseDetails> {
            override fun onFailure(call: Call<ApiResponseDetails>, t: Throwable) {
                //Log.d(TAG, "Error: $t")
                Toast.makeText(this@SongListActivity, "Error: $t",Toast.LENGTH_SHORT).show();
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
                    //Log.d(TAG, "No se envío el tarareo")
                    Toast.makeText(this@SongListActivity, "No se envió el tarareo",Toast.LENGTH_SHORT).show();
                }
            }
        })
    }

}
