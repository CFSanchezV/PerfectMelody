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
import com.christian.perfectmelodysprint2.R
import com.christian.perfectmelodysprint2.adapters.OnSongClickListener
import com.christian.perfectmelodysprint2.adapters.SongAdapter
import com.christian.perfectmelodysprint2.database.SongDB
import com.christian.perfectmelodysprint2.models.Song
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_save.*


class SaveActivity : AppCompatActivity(), OnSongClickListener {
    private val TAG = "SaveActivity"

    lateinit var songs : ArrayList<Song>
    lateinit var songAdapter: SongAdapter

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
            R.id.menu_search -> {
                val intent3 = Intent(this, SongListActivity::class.java)
                intent3.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                startActivityIfNeeded(intent3, 0)
            }
            else -> {
                Log.d(TAG, "Ya está en Favoritos")
            }
        }
        return@OnNavigationItemSelectedListener false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_save)

        btm_navView2.selectedItemId = R.id.menu_favourite
        btm_navView2.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

        loadSongs()
        songAdapter = SongAdapter(songs, this@SaveActivity)
        rvSavedSongs.adapter = songAdapter
        rvSavedSongs.layoutManager = LinearLayoutManager(this@SaveActivity)
    }

    override fun onResume() {
        super.onResume()
        loadSongs()

        songAdapter = SongAdapter(songs, this@SaveActivity)
        rvSavedSongs.adapter = songAdapter
        rvSavedSongs.layoutManager = LinearLayoutManager(this@SaveActivity)

        songAdapter.notifyDataSetChanged()
    }

    private fun loadSongs() {
        songs = SongDB.getInstance(this).getSongDAO().getAllSongs() as ArrayList<Song>
    }

    override fun onItemClicked(position: Int, song: Song, view: View) {
        when(view.id) {
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
                deleteFavourite(position, song)
            }
        }
    }

    private fun deleteFavourite(position: Int, song: Song){
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.app_name)
        builder.setMessage("Desea eliminar la canción de sus favoritos: ${song.name} ?")
        builder.setIcon(android.R.drawable.ic_menu_delete)
        builder.setPositiveButton("Sí"
        ) { dialog, id -> dialog.dismiss()
            //YES
            Log.d("Eliminación de favorita", "Eliminando favorita: ${song.name}")
            SongDB.getInstance(this).getSongDAO().deleteSong(song)
            songAdapter.songs.removeAt(position)
            songAdapter.notifyDataSetChanged()

            Toast.makeText(this, "Eliminada de Favoritos", Toast.LENGTH_SHORT).show()
        }
        builder.setNegativeButton("No"
        ) { dialog, id -> dialog.dismiss()
            //NO
            Toast.makeText(this, "No se eliminó de Favoritos", Toast.LENGTH_SHORT).show()
        }
        val alert: AlertDialog = builder.create()
        alert.show()
    }

}