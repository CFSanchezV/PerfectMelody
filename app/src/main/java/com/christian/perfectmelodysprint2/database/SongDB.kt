package com.christian.perfectmelodysprint2.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.christian.perfectmelodysprint2.models.Song

@Database(entities = [Song::class], version = 1)
abstract class SongDB : RoomDatabase() {
    abstract fun getSongDAO() : SongDAO

    companion object {
        private var INSTANCE : SongDB?= null


        fun getInstance(context: Context) : SongDB {
            if (INSTANCE == null) {
                INSTANCE = Room
                    .databaseBuilder(context, SongDB::class.java, "song.db")
                    .allowMainThreadQueries()
                    .build()
            }
            return INSTANCE as SongDB
        }
    }
}