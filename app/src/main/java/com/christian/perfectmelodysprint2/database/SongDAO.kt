package com.christian.perfectmelodysprint2.database

import androidx.room.*
import com.christian.perfectmelodysprint2.models.Song

@Dao
interface SongDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSong(vararg song: Song)

    @Query("SELECT * FROM songs")
    fun getAllSongs() : List<Song>

    @Query("SELECT * FROM songs WHERE Id = :id")
    fun getById(id : Int): List<Song>

    @Delete
    fun deleteSong(vararg song: Song)

    @Update
    fun updateSong(vararg song: Song)
}