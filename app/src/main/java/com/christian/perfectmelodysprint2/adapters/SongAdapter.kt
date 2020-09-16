package com.christian.perfectmelodysprint2.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.christian.perfectmelodysprint2.R
import com.christian.perfectmelodysprint2.models.Song
import kotlinx.android.synthetic.main.prototype_song.view.*

class SongAdapter(val songs: List<Song>, val songClickListener: OnSongClickListener) : RecyclerView.Adapter<SongPrototype>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongPrototype {
        val view = LayoutInflater.
        from(parent.context).
        inflate(R.layout.prototype_song,parent,false)

        return SongPrototype(view)
    }

    override fun getItemCount(): Int {
        return songs.size
    }

    override fun onBindViewHolder(holder: SongPrototype, position: Int) {
        // Additional: Alternative UI window to select link
        holder.bind(songs[position], songClickListener)
    }
}

class SongPrototype(itemView : View) : RecyclerView.ViewHolder(itemView) {
    val cvSong = itemView.cvSong
    val tvName = itemView.tvName
    val tvArtist = itemView.tvArtist
    val tvConfidence = itemView.tvConfidence

    //link icons
    val btnYoutube = itemView.youtubeBtn
    val btnSpotify = itemView.spotifyBtn
    val btnSoundcloud = itemView.soundCloudBtn


    fun bind(song : Song, songClickListener: OnSongClickListener) {
        tvName.text = song.name
        tvArtist.text = song.artist
        tvConfidence.text = song.confidence.toString()

        btnYoutube.setOnClickListener {
            songClickListener.onItemClicked(song, btnYoutube)
        }
        btnSpotify.setOnClickListener {
            songClickListener.onItemClicked(song, it)
        }
        btnSoundcloud.setOnClickListener {
            songClickListener.onItemClicked(song, it)
        }
    }
}

interface OnSongClickListener {
    fun onItemClicked(song: Song, btn : View)
}