package com.christian.perfectmelodysprint2.activities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.christian.perfectmelodysprint2.R
import com.christian.perfectmelodysprint2.adapters.OnSongClickListener
import com.christian.perfectmelodysprint2.models.Song

class SongListActivity : AppCompatActivity(), OnSongClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_song_list)
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


}
