package com.christian.perfectmelodysprint2.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.christian.perfectmelodysprint2.AudioManager
import com.christian.perfectmelodysprint2.BuildConfig
import com.christian.perfectmelodysprint2.R
import com.christian.perfectmelodysprint2.adapters.OnSongClickListener
import com.christian.perfectmelodysprint2.adapters.SongAdapter
import com.christian.perfectmelodysprint2.database.SongDB
import com.christian.perfectmelodysprint2.models.ApiResponseDetails
import com.christian.perfectmelodysprint2.models.Song
import com.christian.perfectmelodysprint2.services.SongService
import com.google.android.material.bottomnavigation.BottomNavigationView
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
import java.util.*
import kotlin.collections.ArrayList

class SongListActivity : AppCompatActivity(), OnSongClickListener {
    private val TAG = "SongListActivity"
    private val ApiKey = BuildConfig.PERFECTMELODY_API_KEY

    lateinit var songs : ArrayList<Song>
    lateinit var songAdapter: SongAdapter

    private lateinit var audioManager: AudioManager

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when(item.itemId) {
            R.id.menu_home -> {
                val intent1 = Intent(this, MainActivity::class.java)
                intent1.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                startActivityIfNeeded(intent1, 0)
            }
            R.id.menu_preview -> {
                val intent2 = Intent(this, PreviewActivity::class.java)
                intent2.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                startActivityIfNeeded(intent2, 0)
            }
            R.id.menu_favourite -> {
                val intent3 = Intent(this, SaveActivity::class.java)
                startActivity(intent3)
                intent3.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                startActivityIfNeeded(intent3, 0)
            }
            else -> {
                Log.d(TAG, "Ya está en Resultados")
            }
        }
        return@OnNavigationItemSelectedListener false
    }

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
        //audiomanager
        audioManager = AudioManager(this)

        //Toast.makeText(this, "api key es: $ApiKey",Toast.LENGTH_SHORT).show(); //Test ApiKey value
        //emptyUploadFile()  //Working OK

        //uploadFileMultiPart()
        loadDummyData()

        btm_navView.selectedItemId = R.id.menu_search
        btm_navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
    }

    override fun onItemClicked(position: Int, song: Song, btn: View) {
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
            R.id.cvSong -> {
                saveFavourite(song)
            }
        }
    }

    private fun saveFavourite(song: Song){
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.app_name)
        builder.setMessage("Desea guardar la canción: ${song.name} ?")
        builder.setIcon(android.R.drawable.ic_menu_save)
        builder.setPositiveButton("Sí"
        ) { dialog, id -> dialog.dismiss()
            //YES
            Log.d("Insercion de favorita", "Insertando favorita: ${song.name}");

            SongDB.getInstance(this).getSongDAO().insertSong(song)
            Toast.makeText(this, "Agregada a Favoritos", Toast.LENGTH_SHORT).show()
        }
        builder.setNegativeButton("No"
        ) { dialog, id -> dialog.dismiss()
            //NO
            Toast.makeText(this, "No se agregó a Favoritos", Toast.LENGTH_SHORT).show()
        }
        val alert: AlertDialog = builder.create()
        alert.show()
    }

    private fun loadDummyData(){
        songs = ArrayList()
        loadingPanel.visibility = View.GONE

        songs.add(Song("marco", "antonio", 11.66f, 1))
        songs.add(Song("marca", "antonio", 1.66f, 2))
        songs.add(Song("marqy", "antonio", 6.46f, 3))
        songs.add(Song("marcu", "antonio", 6.45f, 4))
        songs.sortWith(Comparator { lhs, rhs ->
            if (lhs.confidence > rhs.confidence) -1 else if (lhs.confidence < rhs.confidence) 1 else 0
        })

        songAdapter = SongAdapter(songs, this@SongListActivity)
        rvSongList.adapter = songAdapter
        rvSongList.layoutManager = LinearLayoutManager(this@SongListActivity)
    }

    //NETWORKING SECTION
    private fun uploadFileMultiPart(){
        val retrofit = Retrofit.Builder()
            .baseUrl("http://18.191.176.24/api/ranking/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val postService = retrofit.create(SongService::class.java)

        //file
        val filePath  = audioManager.filePathForId("audioFile")
        val audioFile = File(filePath)
        //build body
        val requestBody = RequestBody.create(MediaType.parse("audio/*"), audioFile)
        val multipartBody = MultipartBody.Part.createFormData("humming", audioFile.name, requestBody)
        //send
        val myRequest = postService.uploadMultipartSong(ApiKey, multipartBody)

        //handleResponse
        myRequest.enqueue(object : Callback<ApiResponseDetails> {
            override fun onFailure(call: Call<ApiResponseDetails>, t: Throwable) {
                //Log.d(TAG, "Error: $t")
                loadingPanel.visibility = View.GONE
                Toast.makeText(this@SongListActivity, "Error: $t",Toast.LENGTH_SHORT).show();
            }

            override fun onResponse(call: Call<ApiResponseDetails>, response: Response<ApiResponseDetails>) {
                //Show text
                Toast.makeText(this@SongListActivity, "Intentando enviar... ${audioFile.name}", Toast.LENGTH_SHORT).show();
                //Load songList on recyclerView and hide the loading animation
                if(response.isSuccessful) {
                    loadingPanel.visibility = View.GONE

                    songs = response.body()!!.songResults as ArrayList<Song>
                    //sorting in Kotlin
                    songs.sortWith(Comparator { lhs, rhs ->
                        if (lhs.confidence > rhs.confidence) -1 else if (lhs.confidence < rhs.confidence) 1 else 0
                    })
                    songAdapter = SongAdapter(songs, this@SongListActivity)
                    rvSongList.adapter = songAdapter
                    rvSongList.layoutManager = LinearLayoutManager(this@SongListActivity)
                }
                else {
                    loadingPanel.visibility = View.GONE
                    //Log.d(TAG, "No se recibió respuesta")
                    Toast.makeText(this@SongListActivity, "No se recibió respuesta",Toast.LENGTH_SHORT).show();
                }
            }
        })
    }


    private fun emptyUploadFile() {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://18.191.176.24/api/ranking/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val postService = retrofit.create(SongService::class.java)

        val emptyRequest = postService.uploadSongEmpty(apiKey = ApiKey)

        emptyRequest.enqueue(object : Callback<ApiResponseDetails> {
            override fun onFailure(call: Call<ApiResponseDetails>, t: Throwable) {
                //Log.d(TAG, "Error: $t")
                Toast.makeText(this@SongListActivity, "Error: $t",Toast.LENGTH_SHORT).show();
            }

            override fun onResponse(call: Call<ApiResponseDetails>, response: Response<ApiResponseDetails>) {
                //Load songList on recyclerView
                if(response.isSuccessful) {
                    songs = response.body()!!.songResults as ArrayList<Song>
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
